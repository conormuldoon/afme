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
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.agentfactory.cldc.scheduler.Buffer;
/** When agents operate in a peer to peer manner in forming ad-hoc networks,
discovery of the names of agents, addresses of agents, and addresses of
agent platforms becomes an issue. The Discovery Server provides rudimentary yellow and white page services
and has been designed to operate small scale devices. Agents in AFME
can still avail of the standard large scale yellow and white page services 
of Agent Factory, but the discovery server is designed for use in situations
when the entire multi-agent system (including supporting infrastructure) must 
operate on embedded devices.
 * 
 * @author Conor Muldoon
 *
 */
public class DiscServer {

	/**
	 * A constant used to indicate platform registration.
	 */
	public static final byte REG=0;
	/**
	 * A constant used to indicate that an agent has moved to a different platform.
	 */
	public static final byte MV=1;
	/**
	 * A constant used to indicate a platform address request.
	 */
	public static final byte REQ_P=2;
	/**
	 * A constant used to indicate a white page lookup or in other words an agent address request.
	 */
	public static final byte REQ_A=3;
	/**
	 * A constant used to indicate a yellow page registration.
	 */
	public static final byte REG_YP=4;
	/**
	 * A constant used to indicate a yellow page lookup.
	 */
	public static final byte REQ_YP=5;

	Hashtable agent,platform,service;

	/** Creates an instance of DiscServer.
	 * 
	 * @param args the arguments passed to the main method.
	 */
	DiscServer(String[]args){
		agent=new Hashtable();
		platform=new Hashtable();
		service=new Hashtable();
		
		try {
			
			StreamConnectionNotifier ssc = (StreamConnectionNotifier) Connector
			.open("socket://:" + args[0]);
			/*
			String serverAddress = "socket://" + ssc.getLocalAddress()
					+ ':' + args[0];
			System.out.println("Discovery Server Address: " + serverAddress);
			*/
			
			Buffer b=new Buffer();
			for(int i=0;i<4;i++)new DiscThread(b).start();
			
			while(true){
				StreamConnection sc= ssc.acceptAndOpen();
				System.out.println("Socket Received");
				b.insert(new DiscTask(sc));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}catch(ClassCastException e){
			e.printStackTrace();
		}
	}
	

	
	/** Creates an instance of the Discovery Server. The port number
	 * the server is to operate at must be specified as the first
	 * string in args.
	 * 
	 * @param args an array of arguments.
	 */
	public static void main(String[] args) {
		
		if (args.length == 0)
			System.err.println("No port specified");
		else {
			
			new DiscServer(args);
		}
	}



	class DiscTask implements Runnable{
		StreamConnection soc;

		DiscTask(StreamConnection socket){
			soc=socket;
		}

		public void run(){
			try{
			DataInputStream dis=soc.openDataInputStream();
			DataOutputStream dos;
			byte b=dis.readByte();

			synchronized(agent){
				//System.out.println("here "+b);
			switch(b){

			case REG:
				String platName=dis.readUTF();
				String addr=dis.readUTF();
				int prt=dis.readInt();
				platform.put(platName,addr+":"+prt);
				for(int i=dis.readInt();i-->0;){
					agent.put(dis.readUTF(),platName);
				}
				break;

			case MV:
				agent.put(dis.readUTF(),dis.readUTF());
				break;
			case REQ_P:
				dos=soc.openDataOutputStream();
				String pn=dis.readUTF();
				Object st=platform.get(pn);
				//System.out.println("Platform lookup: "+pn);
				if(st==null){
					System.err.println("No platform registered: "+pn);
					System.err.println("Sending: NoSuchPlat");
					dos.writeUTF("NoSuchPlat");
				}else
					dos.writeUTF((String)st);

				//System.out.println("Platform sent");
				dos.close();
				break;
			case REQ_A:
				dos=soc.openDataOutputStream();
				String an=dis.readUTF();
				Object s=agent.get(an);
				if(s==null){
					System.err.println("No agent registered: "+an);
					System.err.println("Sending: NoSuchAgent");
					dos.writeUTF("NoSuchSuch");
				}else
					dos.writeUTF((String)platform.get(s));
				dos.close();
				break;
			case REG_YP:
				/*String as=dis.readUTF();
				String serN=dis.readUTF();
				System.out.println("Reg: "+as+" "+serN);

				service.put(as,serN);*/
				service.put(dis.readUTF(),dis.readUTF());
				break;
			case REQ_YP:
				dos=soc.openDataOutputStream();
				String serv=dis.readUTF();
				Object agt=service.get(serv);
				if(agt==null){
					System.err.println("No service registered: "+serv);
					System.err.println("Sending: NoSuchServ");
					System.err.println("Sending: NoSuchAddress");
					dos.writeUTF("NoSuchSuch");
					dos.writeUTF("NoSuchAddress");
				}else{
					dos.writeUTF((String)agt);
					dos.writeUTF((String)platform.get((agent.get(agt))));
				}
				dos.close();
			}
			//System.out.println("out "+b);
			}
			dis.close();
			soc.close();
			}catch(IOException e){
				e.printStackTrace();
			}

		}
	}

	class DiscThread extends Thread{

		Buffer buf;
		public DiscThread(Buffer b){
			buf=b;
		}

		public void run(){
			while(true){
				Runnable r=buf.extract();

				r.run();
			}
		}
	}
}
