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
import java.io.IOException;

import javax.microedition.io.SocketConnection;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.logic.FOS;

/** The peer to peer task reads an incoming message from a socket and adds
 * the message to the peer to peer message transport service. 
 * 
 * @author Conor Muldoon
 *
 */
public class P2PTask implements Runnable{
	SocketConnection socket;
	PeerToPeerMTS p2pMTS;
	String app;
	Platform p;
	
	/** Creates an instance of the peer to peer task.
	 * 
	 * @param s the incoming socket connection.
	 * @param mts the peer to peer message transport service of the platform.
	 * @param applic the name of the agent platform.
	 * @param plat the local agent platform.
	 */
	P2PTask(SocketConnection s,PeerToPeerMTS mts,String applic,Platform plat){
		
		socket=s;
		app=applic;
		p=plat;
		p2pMTS=mts;
	}
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		//System.out.println("receiving message");
		try{
//		 Code to receive message
		DataInputStream dis=socket.openDataInputStream();
		
		// Sender Addresses
		String senderAdd=dis.readUTF();
		
		// Target Name.
		String tn=dis.readUTF();
		
		// Performative
		String perf=dis.readUTF();
		
		// Sender Name
		String sn=dis.readUTF();
		
		// Content
		String content=dis.readUTF();
		
		dis.close();
		socket.close();
		
		AgentName targetName=new AgentName(FOS.createFOS(tn),app,p);
		AgentName senderName=new AgentName(FOS.createFOS(sn),app,p);
		if(p2pMTS.messageAdd(targetName, perf,
				senderName, senderAdd, FOS.createFOS(content)))
			System.out.println("Message from "+sn+" not delivered. "+tn+" not on platform.");
		
		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println("message received");
	}

}
