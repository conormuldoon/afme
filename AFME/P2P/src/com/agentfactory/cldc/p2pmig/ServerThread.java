
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

package com.agentfactory.cldc.p2pmig;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.migration.MigrationPlatform;
import com.agentfactory.cldc.scheduler.Buffer;

/** The server thread creates a server socket connection to receive
 * incoming socket connections and add incoming agents to the local
 * agent platform.
 * 
 * @author Conor Muldoon
 *
 */
public class ServerThread extends Thread{
	
	ServerSocketConnection ssc;
	boolean active;
	MigrationPlatform plat;
	
	/** Creates an instance of the server thread.
	 * 
	 * @param args the arguments passed to the peer to peer migration manager.
	 * @param mp the local agent platform.
	 */
	public ServerThread(String[]args,MigrationPlatform mp){
		try{
			plat=mp;
			String prt="";
			if(args.length>1)prt=":"+args[1];
			ssc=(ServerSocketConnection)Connector.open("socket://"+prt);
			
			String serverAddress="socket://"+ssc.getLocalAddress()+':'+ssc.getLocalPort();
			System.out.println("Migration Server Address: " +serverAddress);
			active=true;
		}catch(IOException e){
			e.printStackTrace();
		}	
	}
	
	/**
	 * Accepts socket connections and adds incoming agents to the 
	 * local platform.
	 */
	public void run(){
		Buffer b=new Buffer();
		
		for(int i=4;i-->0;)new MigThread(b).start();
		try{
			while(active){
				b.insert(new MigTask(plat,(SocketConnection) ssc.acceptAndOpen()));				
				
			}
		}catch(java.io.IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	class MigTask implements Runnable{
		StreamConnection socket;
		MigrationPlatform plat;
		MigTask(MigrationPlatform mp,StreamConnection soc){
			socket=soc;
			plat=mp;
		}
		public void run(){
			try{
			DataInputStream dis=socket.openDataInputStream();
			
			for(int i=dis.readInt();i-->0;)plat.createAgent(dis);
			
            
			socket.close();
			dis.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	class MigThread extends Thread{
		
		Buffer b;
		public MigThread(Buffer buf){
			b=buf;
		}
		
		public void run(){
			while(true){
			Runnable r=b.extract();
			r.run();
			}
		}
	}

}
