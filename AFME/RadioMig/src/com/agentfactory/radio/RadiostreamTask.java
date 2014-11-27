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

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.migration.MigrationPlatform;

/**
 * The server thread creates a server socket connection to receive incoming
 * socket connections and add incoming agents to the local agent platform.
 * 
 * @author Conor Muldoon
 * 
 */
public class RadiostreamTask implements Runnable {

	String address;
	MigrationPlatform plat;
	RadioServerTask rst;

	/**
	 * Creates an instance of RadiostreamTask.
	 * 
	 * @param addr
	 *            the address.
	 * @param mp
	 *            the agent platform.
	 */
	public RadiostreamTask(String addr, MigrationPlatform mp,
			RadioServerTask tsk) {
		plat = mp;
		address = addr;
		rst = tsk;

	}

	/**
	 * Accept connections and adds incoming agents to the local platform.
	 */
	public void run() {

		try {

			System.out.println("creating stream: " + address);

			StreamConnection conn = (StreamConnection) Connector.open(address);

			// DataOutputStream dos=conn.openDataOutputStream();

			// dos.writeInt(0);
			// dos.close();
			// System.out.println("acc sent 0");
			DataInputStream dis = conn.openDataInputStream();
			System.out.println("Input stream created");
			//int num = dis.readInt();
			// System.out.println("Receiving agent "+num);

			// for (int i = num; i-- > 0;) {
			plat.createAgent(dis);
			// }
			dis.close();

			System.out.println("agent created");
			rst.remove(address);

		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

	}

}
