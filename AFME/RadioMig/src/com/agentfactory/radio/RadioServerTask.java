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
package com.agentfactory.radio;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.agentfactory.cldc.migration.MigrationPlatform;
import com.agentfactory.cldc.scheduler.Scheduler;

/**
 * This class acts as a server to receive incoming datagrams.
 * 
 * @author Conor Muldoon
 * 
 */
public class RadioServerTask implements Runnable {

	// String prt;
	Scheduler sch;
	MigrationPlatform plat;
	DatagramConnection dgc;
	RadioMigManager m;
	boolean active;

	final public static String ACK = "_a_";

	/**
	 * Creates an instance of RadioServerTask.
	 * 
	 * @param port
	 *            the specified port number.
	 * @param sched
	 *            the platform scheduler.
	 * @param migP
	 *            the agent platform.
	 *            
	 *  @param rmm
	 *            the radio migration manager service.          
	 */

	/**
	 * 
	 */
	Vector addr;

	RadioServerTask(String port, Scheduler sched, 
			MigrationPlatform migP,RadioMigManager rmm) {

		m=rmm;
		plat = migP;
		sch = sched;
		addr = new Vector();
		try {
			dgc = (DatagramConnection) Connector.open("radiogram://:" + port);

		} catch (IOException ex) {
			ex.printStackTrace();

		}
		active = true;
		System.out.println("radio server created: " + port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		
		System.out.println("radio server running");
		while (active) {
			try {
				Datagram dg = dgc.newDatagram(dgc.getMaximumLength());
				dgc.receive(dg);
				
				boolean sd = true;

				String s = dg.readUTF();
				
				if (s.equals(ACK)) {

					System.out.println("Ack received");
					m.ackRec();
				} else {

					
					System.out.println("radiogram received");
					String gramPrt=dg.readUTF();
					String address = "radiostream://" + s + ':'
							+ dg.readUTF();
					for (int i = addr.size(); i-- > 0;) {
						if (addr.elementAt(i).equals(address)) {
							sd = false;
							break;
						}
					}
					if (sd) {
						addr.addElement(address);
						sch.schedule(new RadiostreamTask(address, plat, this));

					}
					
					// Send acknowledgement
					
					address = "radiogram://" + s + ':'
							+ gramPrt;
					
					System.out.println("Sending ack: "
							+ address);
					DatagramConnection dconn = (DatagramConnection) Connector
							.open(address);
					Datagram ack = dconn.newDatagram(dconn
							.getMaximumLength());
					ack.writeUTF(ACK);
					

					dconn.send(ack);
					dconn.close();
					System.out.println("Ack sent");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method removes the specified address from a list of addresses that
	 * currently have active radiostreams.
	 * 
	 * @param address
	 *            the radiostream address.
	 */
	public void remove(String address) {
		addr.removeElement(address);
	}

	/**
	 * This method closes the datagram connection.
	 * 
	 */
	public void destroy() {
		active = false;
		try {
			dgc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
