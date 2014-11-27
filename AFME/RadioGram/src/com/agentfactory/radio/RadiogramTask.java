package com.agentfactory.radio;

/**
 * Copyright:   Copyright (c) 1996-2008 The Agent Factory Working Group. All rights reserved.
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

import java.io.IOException;

import javax.microedition.io.Datagram;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.logic.FOS;

/** The RadioTask is executed by a worker thread when there is an incoming message.
 * 
 * @author Conor Muldoon
 *
 */
public class RadiogramTask implements Runnable {

	Datagram dg;

	RadiogramMTS radioMTS;

	String app;

	Platform p;

	/** Creates an instance of RadioTask.
	 * 
	 * @param dataGram the datagram of the incoming message.
	 * @param mts the radiogram MTS of the local platform.
	 * @param application the name of the application.
	 * @param platform the local agent platform.
	 */
	public RadiogramTask(Datagram dataGram, RadiogramMTS mts, String application,
			Platform platform) {
		dg = dataGram;
		radioMTS = mts;
		app = application;
		p = platform;
	}

	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			// Sender Addresses
			String senderAdd = dg.readUTF();

			// Target Name.
			String tn = dg.readUTF();
			
			

			// Performative
			String perf = dg.readUTF();

			// Sender Name
			String sn = dg.readUTF();

			// Content
			String content = dg.readUTF();

			
			AgentName targetName = new AgentName(FOS.createFOS(tn), app, p);
			AgentName senderName = new AgentName(FOS.createFOS(sn), app, p);
			
			radioMTS.messageAdd(targetName, perf, senderName, senderAdd,
					FOS.createFOS(content));
				//System.out.println("Message from " + sn + " not delivered. "
					//	+ tn + " not on platform.");
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}