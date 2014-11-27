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

package com.agentfactory.cldc.p2pmts;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.mts.AID;
import com.agentfactory.cldc.mts.IDSet;
import com.agentfactory.cldc.mts.MTSConstant;
import com.agentfactory.cldc.mts.Message;
import com.agentfactory.cldc.mts.MessageContainer;
import com.agentfactory.cldc.mts.MessageHolder;
import com.agentfactory.cldc.mts.MessageTransportService;
import com.agentfactory.cldc.mts.PendingMessages;
import com.agentfactory.cldc.mts.ServerConstant;
import com.agentfactory.cldc.scheduler.Buffer;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The CDC peer to peer message transport service facilitates communication between agents in AFME in a peer
 * to peer manner.
 * If agents are on the local platform, messages are transferred directly. If agents
 * are not on the same platform, sockets are used.
 * 
 * @author Conor Muldoon
 */
public class CDCPeerToPeerMTS extends Service implements Runnable {

	final static String BLOCK = "block";
	static final byte REG=0;
	
	Hashtable messages, pending, agentIDs;

	Object holderLock;

	MessageHolder[] outgoing;

	boolean checkReg;

	String identifier;

	static final byte SIZE = 8;

	int size;

	String app;

	boolean block;
	boolean active;
	Platform p;
	Scheduler sched;

	StreamConnectionNotifier ssc;
	String localAddress;
	/** Creates an instance of CDCPeerToPeerMTS.
	 * 
	 * @param args a string of arugments that are passed to the service.
	 * @param agtNms the names of the agents on the local platform.
	 * @param scheduler the scheduler that will periodically execute the 
	 * run method of the message transport service.
	 * @param plat the local platform, upon which the agents reside.
	 */
	public CDCPeerToPeerMTS(String[] args, Object[] agtNms,
			Scheduler scheduler,Platform plat) {
		super(args[0]);
		p=plat;
		
		holderLock = new int[0];
		messages = new Hashtable();
		agentIDs = new Hashtable();
		outgoing = new MessageHolder[SIZE];

		pending = new Hashtable();
		
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
		app = args[1];
		
		block=args[2].equals(BLOCK);
		sched=scheduler;
		try{
			String prt=null;
			if(args.length>3&&!args[3].equals("none"))prt=":"+args[3];
			else{ 
				prt=":7777";
				System.out.println("Default Port: "+prt.substring(1));
			}
			ssc=(StreamConnectionNotifier)Connector.open("socket://"+prt);
			localAddress="socket://"+InetAddress.getLocalHost().toString()+':'+prt;
			System.out.println("Local Address: " +localAddress);
			
			if(args.length>4){
				
				System.out.println("Registering with Discovery Server: "+args[4]);
				StreamConnection sc=(StreamConnection)Connector.open(args[4]);
				DataOutputStream dos=sc.openDataOutputStream();
				dos.writeByte(REG);
				dos.writeUTF(app);
				dos.writeUTF(InetAddress.getLocalHost().toString());
				dos.writeInt(Integer.parseInt(prt.substring(1)));
				dos.writeInt(agtNms.length);
				for(int i=agtNms.length;i-->0;){
					StringBuffer sb=new StringBuffer();
					((AgentName)agtNms[i]).appendName(sb);
					dos.writeUTF(sb.toString());
				}
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		active=true;
		new Thread(this).start();
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

	

	/** Receives incomming socket connections and adds messages to the service.
	 * 
	 */
	public void run() {
		Buffer b=new Buffer();
		for(int i=4;i-->0;)new PeerThread(b).start();
		try{
			while(active){
				SocketConnection socket = (SocketConnection) ssc.acceptAndOpen();				
				b.insert(new CDCP2PTask(socket,this,app,p));				
			}
		}catch(java.io.IOException e){
			e.printStackTrace();
		}
	}
	
	private class PeerThread extends Thread{
		
		Buffer buf;
		PeerThread(Buffer b){
			buf=b;
		}
		public void run(){
			
			
			while(active){
				
				Runnable tt=buf.extract();
				tt.run();
			}
			
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

	/** Performs an action on the peer to peer message transport service. There are size
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
			
			final FOS cl= checkLocal(action, agentName, Message.INFORM);
			if(cl!=null){
				if(block)return cl;
				else{
				sched.schedule(new Runnable(){
					public void run(){
						send(cl, Message.INFORM);
					}
				});
				
				}
			}return null;
			
		case MTSConstant.REQUEST:
			
			final FOS chkl=checkLocal(action, agentName, Message.REQUEST);
			
			if(chkl!=null){
				
				if(block)return chkl;
				else{
				
				sched.schedule(new Runnable(){
					public void run(){
						
						send(chkl, Message.REQUEST);
					}
				});
				
				}
			}
			
			return null;
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
				pendMess.sendMessages(sched,localAddress, fos, aid, agentName);
			}
			return null;

		case MTSConstant.UPDATE_ID:

			return null;
		default: // MTSConstant.REGISTER_AGENT
			System.err.println("Register Agent not support in P2P MTS.");
			return null;
		}

	}
	/** Adds a message to the message container of the target agent.
	 * 
	 * @param targetName the name of the agent the message is intended for.
	 * @param performative the performative of the message.
	 * @param senderName the name of the agent that sent the message.
	 * @param url the address of the agent that sent the message.
	 * @param content the information content of the message.
	 * @return true if there is no agent container for the target agent, false otherwise.
	 */
	public boolean messageAdd(AgentName targetName, String performative, AgentName senderName,
			String url, FOS content) {
		
		if(targetName.isWild()){
			Enumeration keys=messages.keys();
			boolean b=true;
			while(keys.hasMoreElements()){
				AgentName an=(AgentName)keys.nextElement();
				if(an.wildEquals(targetName)){
					((MessageContainer) messages.get(an)).addMessage(new Message(performative, senderName, url, content));
					b=false;
				}
			}
			return b;
			
		}else{
			MessageContainer container = (MessageContainer) messages.get(targetName);
			if(container==null)return true;
			container.addMessage(new Message(performative, senderName, url, content));
		}
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
						agentName, localAddress, content)){
					
					// sender address
					FOS address=FOS.createFOS(localAddress);
					// target address addresses(?url)
					FOS url=fos.next().next();
					
					return MessageTransportService.createOutMessage(url.toString(),address,name,agentName,content);
					}
				return null;

			}
		}
		
		AgentName targetName=new AgentName(fos,app,p);
		if(!messageAdd(targetName, performative,
				agentName, localAddress, content))return null;
		
		// Process Wildcard IDs
		if (targetName.isWild()) {
			Enumeration e = messages.keys();
			boolean ret = false;

			while (e.hasMoreElements()) {
				AgentName name = (AgentName) e.nextElement();
				// Don't send message to self
				//if(name==agentName)continue;
				
				if (name.wildEquals(targetName)) {
					MessageContainer container = (MessageContainer) messages.get(name);
					container.addMessage(new Message(performative, agentName, localAddress,
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
			FOS message= idSet.createMessage(localAddress, fos, performative, agentName, content,
					pending);
			
			if(message!=null){
				send(message,performative);
			}
			return null;
		}
	}
	
	 static boolean send(FOS fos,String perf){
	       // System.out.println("sending "+fos);
	    	try{
	        	
	            StreamConnection conn = (StreamConnection) Connector.open(fos.next().toString());
	            
	            DataOutputStream dos=conn.openDataOutputStream();
	            

	            dos.writeUTF(fos.next().toString());
	            dos.writeUTF(fos.next().toString());
	            dos.writeUTF(perf);
	            dos.writeUTF(fos.next().toString());
	            dos.writeUTF(fos.next().toString());
	            conn.close();
	            dos.close();
	        }catch(ConnectionNotFoundException cnfe){
	            cnfe.printStackTrace();
	            return false;
	        }catch( IOException e ){
	            e.printStackTrace();
	            return false;
	        }
	        //System.out.println("message sent");
	        return true;
	    }
	
	
	
}
