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

/*
 * MigrationService.java
 *
 * Created on 20 June 2005, 13:06
 */

package com.agentfactory.radio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.migration.MigrationPlatform;
import com.agentfactory.cldc.migration.OutAgent;
import com.agentfactory.cldc.scheduler.Scheduler;

/**
 * The radiostream migration manager is a service that is responsible for
 * controlling the migration process in a wireless sensor networks using
 * radiostreams.
 * 
 * @author Conor Muldoon
 */

public class RadioMigManager extends Service implements Runnable {

	MigrationPlatform plat;

	int size;

	boolean noAck;
	final static byte SIZE = 4;

	OutAgent[] outAgt;

	String[] outAddr;

	String migPort;
	String gramPrt;

	Scheduler sched;

	boolean scheduled;

	RadioServerTask rst;

	/**
	 * Creates a new instance of P2PMigrationService.
	 * 
	 * @param s
	 *            arguments to the migration service.
	 * @param an
	 *            the names of the agents on the platform.
	 * @param scheduler
	 *            the scheduler periodically executes the run method of the
	 *            MigrationManager.
	 * @param platform
	 *            the local migration platform, on which the agents reside.
	 */

	String[] args;

	public RadioMigManager(String[] s, AgentName[] an, Scheduler scheduler,
			MigrationPlatform platform) {
		super(s[0]);

		plat = platform;
		outAgt = new OutAgent[SIZE];
		outAddr = new String[SIZE];
		sched = scheduler;

		migPort = s[2];
		gramPrt = s[1];
		rst = new RadioServerTask(gramPrt, sched, plat, this);
		sched.schedule(rst);
		args = s;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agentfactory.cldc.Service#destroy()
	 */
	public void destroy() {
		rst.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agentfactory.cldc.Service#modifyBinding(java.lang.Object,
	 * java.lang.Object)
	 */
	public void modifyBinding(Object oldName, Object newName) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.agentfactory.cldc.Service#processPer(com.agentfactory.cldc.AgentName,
	 * int)
	 */
	public FOS processPer(AgentName agentName, int perceptionID) {
		return null;
	}

	public synchronized void ackRec() {
		notify();
		noAck = false;
	}

	/**
	 * Add the agent with the specified name to the outgoing list.
	 * 
	 * @param agentName
	 *            the name of the agent to be added to the outgoing list.
	 * @param actionID
	 *            the actionID is not used in this instance of processAction.
	 * @param data
	 *            represents the destination and design URL of the agent.
	 */
	public FOS processAction(AgentName agentName, int actionID, FOS data) {
		// System.out.println("here");
		synchronized (this) {
			if (size == outAgt.length) {

				OutAgent[] newAgt = new OutAgent[outAgt.length << 1];
				String[] newURL = new String[outAgt.length << 1];
				System.arraycopy(outAgt, 0, newAgt, 0, size);
				System.arraycopy(outAddr, 0, newURL, 0, size);
				outAgt = newAgt;
			}
			plat.emigrate(agentName);
			String destAddr = data.next().toString();
			outAgt[size] = new OutAgent(agentName, destAddr, data.next()
					.toString());
			outAddr[size] = destAddr;
			size++;
			if (!scheduled) {
				scheduled = true;
				sched.schedule(this);
			}

		}

		return null;
	}

	/**
	 * Sends outgoing agents to their target platforms.
	 */
	public void run() {

		try {
			synchronized (this) {

				int ts = size;
				for (int i = ts; i-- > 0;) {

					final String add = outAddr[i];
					final OutAgent oagt = outAgt[i];

					// DataInputStream dis = null;
					DataOutputStream dos = null;
					try {

						String streamAdd = "radiostream://"
								+ add.substring(0, add.indexOf(':') + 1)
								+ migPort;
						StreamConnection conn = (StreamConnection) Connector
								.open(streamAdd);
						// dis = conn.openDataInputStream();
						dos = conn.openDataOutputStream();

						System.out.println("Stream created: " + streamAdd);

						noAck = true;
						while (noAck) {
							System.out.println("Sending datagram: "
									+ outAddr[i]);
							DatagramConnection dconn = (DatagramConnection) Connector
									.open("radiogram://" + outAddr[i]);
							Datagram dg = dconn.newDatagram(dconn
									.getMaximumLength());
							if (args.length == 4) {
								dg.writeUTF(args[3]);
							} else
								dg.writeUTF(""
										+ com.sun.spot.peripheral.Spot
												.getInstance()
												.getRadioPolicyManager()
												.getIEEEAddress());
							dg.writeUTF(gramPrt);
							dg.writeUTF(migPort);

							dconn.send(dg);
							dconn.close();
							System.out.println("Datagram sent");
							wait(com.sun.spot.peripheral.RadioConnectionBase.DEFAULT_TIMEOUT);
						}

						// dis.readInt();
						// dis.close();

						//dos.writeInt(1);
						//dos.flush();
						oagt.write(dos, plat, false);

						dos.flush();
						
						dos.close();

						conn.close();

						outAgt[i] = null;
						outAddr[i] = null;

						System.out.println("agent sent");
					} catch (IOException e) {

						System.err.println("Problem sending agent");
						e.printStackTrace();
						// if (dis != null)
						// dis.close();
						if (dos != null)
							dos.close();
					}

				}
				int dff = size - ts;
				for (int i = 0; i < dff; i++) {
					outAgt[i] = outAgt[ts + i];
					outAgt[ts + i]=null;
					outAddr[i] = outAddr[ts + i];
					outAddr[ts + i]=null;
				}
				size = dff;
				if (size == 0)
					scheduled = false;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
