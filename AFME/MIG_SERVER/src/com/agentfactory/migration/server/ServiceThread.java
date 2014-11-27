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
 * ServiceThread.java
 *
 * Created on 29 June 2005, 15:58
 */

package com.agentfactory.migration.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collection;

import com.agentfactory.platform.exception.AgentConfigurationException;
import com.agentfactory.platform.exception.MalformedLogicException;
import com.agentfactory.platform.logic.FOS;
import com.agentfactory.platform.logic.LogicFactory;
import com.agentfactory.plugins.interpreters.afapl2.AFAPLAgent;
import com.agentfactory.plugins.interpreters.afapl2.control.RoleLibrary;
import com.agentfactory.plugins.interpreters.afapl2.control.RoleTemplate;
import com.agentfactory.plugins.interpreters.afapl2.mentalOject.CommitmentRule;
import com.agentfactory.plugins.interpreters.afapl2.util.InterpreterHelper;
import com.agentfactory.plugins.parser.afapl2.parseTree.CommitNode;
import com.agentfactory.plugins.parser.afapl2.parseTree.ImpliesNode;
import com.agentfactory.plugins.parser.afapl2.parseTree.ProgramNode;
import com.agentfactory.plugins.parser.afapl2.reader.Reader;
import com.agentfactory.plugins.parser.afapl2.reader.StringReader;
import com.agentfactory.plugins.services.ams.AgentManagementService;
import com.agentfactory.plugins.services.mts_mpp.SocketBuffer;

/**
 * 
 * @author Conor Muldoon
 */
public class ServiceThread extends Thread {

	SocketBuffer buffer;

	AgentManagementService service;

	/** Creates a new instance of ServiceThread */
	public ServiceThread(SocketBuffer buf, AgentManagementService serv) {
		buffer = buf;
		service = serv;
	}

	public void run() {
		while (true) {
			Socket socket = buffer.extract();

			DataInputStream input = null;
			try {
				input = new DataInputStream(socket.getInputStream());
				StringBuilder buffer = new StringBuilder();

				// incoming design
				for (int i = input.readInt(); i-- > 0;) {
					buffer.append(input.readUTF());
					// buffer.append(";");
				}

				StringBuilder newBuilder = new StringBuilder();
				// incoming role
				for (int i = input.readInt(); i-- > 0;) {

					BufferedReader roleRead = new BufferedReader(
							new InputStreamReader(ClassLoader
									.getSystemClassLoader()
									.getResourceAsStream(input.readUTF())));
					String roleLine = null;
					while ((roleLine = roleRead.readLine()) != null)
						newBuilder.append(roleLine);
					roleRead.close();
				}

				// incoming import

				for (int i = input.readInt(); i-- > 0;) {

					String s = input.readUTF();

					BufferedReader importRead = new BufferedReader(
							new InputStreamReader(ClassLoader
									.getSystemClassLoader()
									.getResourceAsStream(s)));
					String roleLine = null;
					while ((roleLine = importRead.readLine()) != null)
						newBuilder.append(roleLine);
					importRead.close();
				}

				// necessary for outgoing filtering
				newBuilder.append(buffer.toString());

				// incoming beliefs
				int size = input.readInt();
				String[] beliefs = new String[size];
				for (int i = size; i-- > 0;)
					beliefs[i] = input.readUTF();

				// incoming retract beliefs
				int retSize = input.readInt();
				String[] retract = new String[retSize];
				for (int i = retSize; i-- > 0;)
					retract[i] = input.readUTF();

				int numTemp = input.readInt();
				RoleTemplate[] template = new RoleTemplate[numTemp];
				try {
					for (int i = numTemp; i-- > 0;) {
						template[i] = new RoleTemplate((FOS) LogicFactory
								.create(input.readUTF()));
						int numRule = input.readInt();
						StringBuilder builder = new StringBuilder();
						for (int j = numRule; j-- > 0;) {
							builder.append(input.readUTF());
							// template[i].addCommitmentRule((TerImplication)LogicFactory.create(input.readLine()));
						}

						Reader reader = new StringReader(builder.toString());
						ProgramNode node = InterpreterHelper
								.generateParseTree(reader);
						Collection c = node.getCommitmentRules();
						for (Object o : c) {
							ImpliesNode rule = (ImpliesNode) o;
							template[i].addCommitmentRule(new CommitmentRule(
									rule.getAndNode(), (CommitNode) rule
											.getLogicNode()));
						}
						int numTrig = input.readInt();
						for (int j = numTrig; j-- > 0;)
							template[i].addTrigger(LogicFactory.create(input
									.readUTF()));
					}
				} catch (MalformedLogicException e) {
					e.printStackTrace();
				} catch (AgentConfigurationException e) {
					e.printStackTrace();
				}
				// Create Agent

				AFAPLAgent agent = (AFAPLAgent) service.createAgentFromDesign(
						input.readUTF(), newBuilder.toString(), "afapl2");
				RoleLibrary roleLib = agent.getRoleLibrary();
				for (int i = numTemp; i-- > 0;)
					roleLib.addRoleTemplate(template[i]);
				loop1: for (int i = size; i-- > 0;) {
					// System.out.println("adding "+beliefs[i]);
					for (int j = retSize; j-- > 0;)
						if (retract[j].equals(beliefs[i]))
							continue loop1;
					agent.addBelief(beliefs[i]);
				}

				/*
				 * Fix me: MigrationController should work with AFAPL2 to set
				 * incomming agent response times MigrationController
				 */
				 try{
					 MigrationController controller=(MigrationController)agent.getController(); //
					 controller.receiveData(input);
				 }catch(ClassCastException e){
					 System.out.println("Agent does not contain the correct controller.");
					 System.out.println("Agent will operate with default sleep time");
				 }
				
				 
				agent.start();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
