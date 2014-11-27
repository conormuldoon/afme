/**
 * Copyright:   Copyright (c) 1996-2006 The Agent Factory Working Group. All rights reserved.
 * Licence:     This file is free software; you can redistribute it and/or modify
 *              it under the terms of the GNU Lesser General Public License as published by
 *              the Free Software Foundation; either version 2.1, or (at your option)
 *              any later version.
 *
 *              This file is distributed in the hope that it will be useful,
 *              but WITHOUT ANY WARRANTY; without even the implied warranty of
 *              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *              GNU Lesser General Public License for more details.
 *
 *              You should have received a copy of the GNU Lesser General Public License
 *              along with Agent Factory Micro Edition; see the file COPYING.  If not, write to
 *              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *              Boston, MA 02111-1307, USA.
 */

package com.agentfactory.cldc.mts;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The message transport service facilitates communication between agents in AFME.
 * If agents are on the local platform, messages are transferred directly. If agents
 * are not on the same platform sockets are used. The message transport service
 * connects to a remote server to receive incoming messages. If the service
 * is operating in synchronous mode, outgoing messages are also sent through
 * the message server. If the service is operating in asynchronous mode, 
 * messages are sent in their own socket.
 * 
 * @author Conor Muldoon
 */
public class MessageTransportService extends Service implements Runnable {

	final static String ASYNC = "ASYNC";

	Hashtable messages, pending, agentIDs;

	String url;

	Object holderLock;

	MessageHolder[] outgoing;

	boolean checkReg;

	String identifier;

	static final byte SIZE = 8;

	int size;

	String app;

	//String storeName;

	boolean async;
	
	Platform p;
	
	private static final String REG="Reg";

	/** Creates an instance of MessageTransportService.
	 * 
	 * @param args a string of arguments that are passed to the service.
	 * @param agtNms the names of the agents on the local platform.
	 * @param scheduler the scheduler that will periodically execute the 
	 * run method of the message transport service.
	 * @param plat the local platform, upon which the agents reside.
	 */
	public MessageTransportService(String[] args, Object[] agtNms,
			Scheduler scheduler,Platform plat) {
		super(MTSConstant.MTS);
		System.out.println("Delete registeredPlatforms.xml and the MTS RMS records on first use");
		p=plat;
		if (args.length == 5)
			scheduler
					.schedule(MTSConstant.MTS, this, Integer.parseInt(args[4]));
		holderLock = new int[0];
		messages = new Hashtable();
		agentIDs = new Hashtable();
		outgoing = new MessageHolder[SIZE];

		pending = new Hashtable();
		url = "socket://" + args[0] + ":" + args[1];
		Object name;
		for (int i = agtNms.length; i-- > 0;) {
			name = agtNms[i];
			messages.put(name, new MessageContainer());
			IDSet set=new IDSet();
			agentIDs.put(name, set);
			pending.put(name, new PendingMessages());
			p.addIDs(name,set);
		}
		checkReg = true;
		app = args[2];
		
		async = args[3].equals(ASYNC);

	}

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.Service#modifyBinding(java.lang.Object, java.lang.Object)
	 */
	public void modifyBinding(Object oldName, Object newName) {
		if (oldName == null) {
			messages.put(newName, new MessageContainer());
			agentIDs.put(newName, new IDSet());
			pending.put(newName, new PendingMessages());
			return;
		}
		if (newName == null) {
			messages.remove(oldName);
			agentIDs.remove(oldName);
			pending.remove(oldName);
			return;
		}
		Object object = messages.get(oldName);
		Object ids = agentIDs.get(oldName);
		Object pend = pending.get(oldName);
		messages.remove(oldName);
		agentIDs.remove(oldName);
		pending.remove(oldName);
		messages.put(newName, object);
		agentIDs.put(newName, ids);
		pending.put(newName, pend);
	}

	/** If the service is operating in asynchronous mode, returns a first
	 * order structure that represent a message that is to be sent. Otherwise,
	 * a message holder is create and the message holder is stored in the
	 * outgoing messages list. When the service connects to the message server,
	 * the message will be sent accordingly.
	 * 
	 * @param name the name of the agent that the message is to be sent to.
	 * @param performative the performative of the message.
	 * @param an the name of the agent that sent the message.
	 * @param content the information content of the message.
	 * @param addresses the address of the agent that sent the message.
	 * @return a FOS representation of the message if operating
	 * in asynchronous mode, otherwise null.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public FOS addHolder(FOS name, String performative, AgentName an,
			FOS content, FOS addresses) throws MalformedLogicException {
		if (async) {
			return createOutMessage(url,addresses,name,an,content);
		
		}
		MessageHolder holder = new MessageHolder(name, new Message(
				performative, an, content), addresses);
		addresses.reset();
		if (size == outgoing.length) {
			MessageHolder[] array = new MessageHolder[outgoing.length + SIZE];
			System.arraycopy(outgoing, 0, array, 0, outgoing.length);
			outgoing = array;
		}
		outgoing[size] = holder;
		size++;
		return null;
	}

	/** The message transport service periodically connect to a message server.
	 * The run method registers the service with the server the first time the
	 * platform executes. It subsequently polls the message server to receive 
	 * incoming messages. If the service is operating is synchronous mode,
	 * outgoing messages are also sent through the server.
	 * 
	 */
	public void run() {
		if (checkReg) {
			checkReg = false;
			identifier=p.newStore(REG);
			
		}
		StreamConnection conn = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		try {
			// System.out.println("a");
			// System.out.println("opening "+url);
			conn = (StreamConnection) Connector.open(url);
			// System.out.println("b");
			dos = conn.openDataOutputStream();
			dis = conn.openDataInputStream();
			// System.out.println("c");
			if (identifier == null) {
				dos.writeUTF(ServerConstant.REGISTER);
				Enumeration enumer = messages.keys();
				while (enumer.hasMoreElements())
					((AgentName) enumer.nextElement()).write(dos);

				dos.writeUTF(ServerConstant.END_REGISTER);
				dos.flush();
				identifier = dis.readUTF();
				sendOut(dos, dis, conn);
				
				p.addData(identifier,REG);
			} else {
				//System.out.println("connecting");
				
				dos.writeUTF(ServerConstant.CHECK_MAIL);
				
				dos.writeUTF(identifier);
				// System.out.println("ident "+identifier);
				dos.flush();
				String line;
				while (!(line = dis.readUTF()).equals(ServerConstant.END_MAIL)) {
					
					try {
						
						AgentName an=new AgentName(FOS.createFOS(line), app,p);
					
						Message message=new Message(dis, app,p);
						if(an.isWild()){
							
							Enumeration keys=messages.keys();
							
							while(keys.hasMoreElements()){
								AgentName agtN=(AgentName)keys.nextElement();
								
								if(agtN.wildEquals(an)){
									((MessageContainer) messages
											.get(agtN)).addMessage(message);
									 
								}
							}
						}else ((MessageContainer) messages
						.get(an)).addMessage(message);
						
					} catch (MalformedLogicException e) {
						e.printStackTrace();
					}
				}
				sendOut(dos, dis, conn);
			}

		} catch (ConnectionNotFoundException cnfe) {
			// System.out.println("Error: "+cnfe.toString());
			cnfe.printStackTrace();
		} catch (IOException e) {
			// System.out.println("Error: "+e.toString());
			e.printStackTrace();
		}
	}

	/** Sends messages to the specified data output stream and once finished closes
	 * the data output stream, data input stream, and stream connection.
	 * 
	 * @param dos the data output stream to write messages to and subsequently close.
	 * @param dis the data input stream to close.
	 * @param conn the stream connection to close.
	 * @throws IOException if there is an I/O error.
	 */
	public void sendOut(DataOutputStream dos, DataInputStream dis,
			StreamConnection conn) throws IOException {
		synchronized (holderLock) {
			for (int i = size; i-- > 0;) {
				//System.out.println("sending message");
				outgoing[i].write(dos);
				outgoing[i] = null;
			}
			size = 0;
		}
		dos.writeUTF(ServerConstant.END_MAIL);
		dos.flush();
		conn.close();
		dis.close();
		dos.close();
	}

	/** Enables the agent to perceive information pertaining to message transport.
	 * Four different types of information may be perceived. (1) Messages that the 
	 * agent has received. (2) Agent IDs that the agent requires or has used. (3)
	 * Messages that are pending delivery. (4) The local residents on the 
	 * platform.
	 */
	public FOS processPer(AgentName agentName, int perceptionID)
			throws MalformedLogicException {

		switch (perceptionID) {
		case MTSConstant.MESSAGE:
			MessageContainer container = (MessageContainer) messages
					.get(agentName);
			
			
			return container.createMessageFOS();
		case MTSConstant.ID:

			IDSet set = (IDSet) agentIDs.get(agentName);
			return set.createIDFOS();

		case MTSConstant.PENDING:
			PendingMessages pendMess = (PendingMessages) pending.get(agentName);
			return pendMess.contstructPending();
		case MTSConstant.RESIDENT:

			Enumeration enumer = messages.keys();
			if (enumer.hasMoreElements()) {
				StringBuffer buffer = new StringBuffer("res(");
				nextRes(buffer, enumer);
				while (enumer.hasMoreElements()) {
					buffer.append(',');
					nextRes(buffer, enumer);
				}
				buffer.append(')');
				return FOS.createFOS(buffer.toString());
			}

		}
		return null;
	}

	void nextRes(StringBuffer buf, Enumeration enumer) {
		buf.append("resident(");
		((AgentName) enumer.nextElement()).appendName(buf);
		buf.append(')');
	}

	/** Performs an action on the message transport service. There are size
	 * different action that may be performed. (1) An agent can be informed
	 * of a message. (2) A request can be made to an agent. (3) An agent ID can 
	 * be saved to a persistent storage medium. (4) An agent ID can be added. (5) An agent ID can be updated.
	 * (6) An agent can be registered.
	 * 
	 * @param agentName the name of the agent that is performing the action.
	 * @param actionID the ID of the action that is to be performed.
	 * @param action that data that is to be used in performing the action.
	 * 
	 */
	
	public FOS processAction(AgentName agentName, int actionID, FOS action)
			throws MalformedLogicException {

		switch (actionID) {

		case MTSConstant.INFORM:
			
			return checkLocal(action, agentName, Message.INFORM);
			
			
		case MTSConstant.REQUEST:
			return checkLocal(action, agentName, Message.REQUEST);
		case MTSConstant.SAVE_ID:
			
			FOS f = action.next();
			AID agtID = new AID(f, action.next());
			StringBuffer sb=new StringBuffer();
			agentName.appendName(sb);
			sb.append(' ');
			agtID.appendBeliefString(sb);
			p.saveID(sb.toString());
			return null;
		case MTSConstant.ADD_ID:

			IDSet idSet = (IDSet) agentIDs.get(agentName);

			FOS fos = action.next();
			AID aid = new AID(fos, action.next());
			idSet.addID(aid);
			PendingMessages pendMess = (PendingMessages) pending.get(agentName);
			synchronized (holderLock) {
				pendMess.sendOut(this, fos, aid, agentName);
			}
			return null;

		case MTSConstant.UPDATE_ID:

			return null;
		default: // MTSConstant.REGISTER_AGENT

			StringBuffer buf = new StringBuffer("regData(");
			buf.append(url);
			buf.append(',');
			buf.append(identifier);
			buf.append(',');
			agentName.appendName(buf);
			buf.append(')');
			return FOS.createFOS(buf.toString());
		}

	}
	private boolean messageAdd(AgentName an, String performative, AgentName agentName,
			String url, FOS content) {
		
		MessageContainer container = (MessageContainer) messages.get(an);
		if(container==null)return true;
		container.addMessage(new Message(performative, agentName, url, content));
		return false;
	}

	private FOS checkLocal(FOS action, AgentName agentName, String performative)
			throws MalformedLogicException {

		FOS fos = action.next();
		FOS content = action.next();
		

		if (fos.functorEquals("agentID")) {
			synchronized (holderLock) {
				FOS name = fos.next();
				
				AgentName an=new AgentName(name,app,p);
				if (messageAdd(an, performative,
						agentName, url, content))
					return addHolder(name, performative, agentName, content,
							fos.next());
				return null;

			}
		}
		
		AgentName an=new AgentName(fos,app,p);
		if(!messageAdd(an, performative,
				agentName, url, content))return null;
		
		// Process Wildcard IDs
		if (an.isWild()) {
			Enumeration e = messages.keys();
			boolean ret = false;

			while (e.hasMoreElements()) {
				AgentName name = (AgentName) e.nextElement();
				// Don't send message to self
				//if(name==agentName)continue;
				
				if (name.wildEquals(an)) {
					MessageContainer container = (MessageContainer) messages.get(name);
					container.addMessage(new Message(performative, agentName, url,
							content));
					
					ret = true;
				}
			}
			if (ret)
				return null;
		}

		// Agent ID not specified in fos
		IDSet idSet = (IDSet) agentIDs.get(agentName);
		synchronized (holderLock) {
			return idSet.sendOut(this, fos, performative, agentName, content,
					pending);
		}
	}
	/** Creates a FOS representation of the outgoing
	 * message.
	 * 
	 * @param serverAddress the address of the server to where the message is being sent.
	 * @param agentAddresses the sender address.
	 * @param name the name of the agent the message is being sent to.
	 * @param senderName the name of the agent sending the message.
	 * @param content the content of the message.
	 * @return the message to be sent.
	 */
	public static FOS createOutMessage(String serverAddress,FOS agentAddresses,FOS name, AgentName senderName,FOS content){
		StringBuffer buf = new StringBuffer("message(");
		buf.append(serverAddress);
		buf.append(',');
		
		agentAddresses.append(buf);
		buf.append(',');
		
		name.append(buf);
		buf.append(',');
		
		senderName.appendName(buf);
		buf.append(',');
		
		content.append(buf);
		buf.append(')');
		
		return FOS.createFOS(buf.toString());
	}
}
