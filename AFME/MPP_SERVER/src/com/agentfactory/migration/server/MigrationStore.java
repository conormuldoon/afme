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
 * MigrationStore.java
 *
 * Created on 28 June 2005, 11:31
 */

package com.agentfactory.migration.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Conor Muldoon
 */
public class MigrationStore {

	Map<String, Collection<Agent>> platformMap;

	File file;

	long num;

	String prt;

	/** Creates a new instance of MigrationStore */
	public MigrationStore(String p) {
		prt = p;
		platformMap = new HashMap<String, Collection<Agent>>();
		file = new File("MigationPlatNum.bin");
		DataInputStream dis = null;
		DataOutputStream dos = null;

		try {
			if (!file.exists()) {
				file.createNewFile();
				num = 0;
				dos = new DataOutputStream(new FileOutputStream(file));
				dos.writeLong(-1);
			} else {
				dis = new DataInputStream(new FileInputStream(file));
				num = dis.readLong();
				num++;
				for (long l = num; l-- > 0;)
					platformMap.put(String.valueOf(l), new ArrayList<Agent>());
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized void storeAgent(String platform, Agent agent)
			throws IOException {
		System.out.println("processing " + platform);
		String[] str = platform.split(":");
		InetAddress dest = InetAddress.getByName(str[0]);
		InetAddress local = InetAddress.getLocalHost();
		if (dest.equals(local) && prt.equals(str[1])) {
			Collection<Agent> coll = platformMap.get(str[2]);
			if (coll == null) {
				newPlat(str[2]);
				coll = platformMap.get(str[2]);
			}
			coll.add(agent);
			System.out.println("agent stored");
		} else{
			Socket s=new Socket(str[0],Integer.parseInt(str[1]));
			DataOutputStream output=new DataOutputStream(s.getOutputStream());
			output.writeInt(1);
			output.writeUTF(platform);
			//  To do: fix design url code
	    	output.writeUTF("");
			agent.send(output);
			output.writeUTF(MigrationWorker.FORWARD);
			output.close();
			s.close();
			System.out.println("agent transferred to: " + str[0] + ":" + str[1]);
			
		}
	}

	void newPlat(String string) throws IOException {
		platformMap.put(string, new ArrayList<Agent>());
		try {
			int val = Integer.parseInt(string);

			if (val >= num) {
				num = val;
				save();
			}
		} catch (NumberFormatException e) {

		}
	}

	public synchronized void sendAgents(String string, DataOutputStream dos)
			throws IOException {
		//System.out.println("checking: "+string);
		Collection<Agent> coll = platformMap.get(string);
		if (coll == null) {
			newPlat(string);
			dos.writeInt(0);
		} else {
			dos.writeInt(coll.size());
			for (Agent agent : coll)
				agent.send(dos);
			coll.clear();
		}
	}

	void save() throws IOException {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(file));
			dos.writeLong(num);
			num++;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dos != null)
					dos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void registerNew(DataOutputStream output)
			throws IOException {
		System.out.println("Registering Platform: " + num);
		platformMap.put(String.valueOf(num), new ArrayList<Agent>());
		output.writeLong(num);
		output.flush();
		save();

	}

}
