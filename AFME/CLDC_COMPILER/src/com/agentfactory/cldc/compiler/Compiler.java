// Copyright:   Copyright (c) 1996-2006 The Agent Factory Working Group. All rights reserved.
// Licence:     This file is free software; you can redistribute it and/or modify
//              it under the terms of the GNU Lesser General Public License as published by
//              the Free Software Foundation; either version 2.1, or (at your option)
//              any later version.
//
//              This file is distributed in the hope that it will be useful,
//              but WITHOUT ANY WARRANTY; without even the implied warranty of
//              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//              GNU Lesser General Public License for more details.
//
//              You should have received a copy of the GNU Lesser General Public License
//              along with Agent Factory Micro Edition; see the file COPYING.  If not, write to
//              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//              Boston, MA 02111-1307, USA.

/*
 * Main.java
 *
 * Created on 26 January 2005, 14:12
 */

package com.agentfactory.cldc.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 
 * @author Conor Muldoon
 */
public class Compiler {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		java.util.List<Role> roles = new java.util.ArrayList<Role>();
		Set<Agent> agents = new HashSet<Agent>();
		Set<InitialBelief> initialBeliefs = new HashSet<InitialBelief>();
		Set<String> starters = new HashSet<String>();
		Collection<Service> services = new ArrayList<Service>();
		InputStream is;
		String let, fileName, platform, classpath;
		GraphicalInterface gi = null;
		platform = "com/agentfactory/cldc/compiler/platform.template";
		let = "com/agentfactory/cldc/compiler/let.template";
		String numThread = "3";
		Map<String, String> moduleMap = new HashMap<String, String>();

		classpath = null;
		fileName = args[args.length - 1];
		System.out.println("File Name: " + fileName);
		for (int i = args.length - 1; --i >= 0;) {
			if (args[i].equals("--let")) {
				let = args[i + 1];

			} else if (args[i].equals("--platform")) {
				platform = args[i + 1];

			} else if (args[i].equals("--classpath")) {
				classpath = args[i + 1];
			}
		}
		System.out.println("MIDlet Template: " + let);
		System.out.println("Platform Template: " + platform);

		URLClassLoader loader = null;
		try {
			if (classpath != null) {
				StringTokenizer tok = new StringTokenizer(classpath, ";");
				int numToks = tok.countTokens();

				URL[] urls = new URL[numToks];
				for (int i = numToks; --i >= 0;) {
					String string = tok.nextToken();
					urls[i] = new File(string).toURL();
					System.out.println("Classpath member: " + string);
				}
				is = (loader = new URLClassLoader(urls))
						.getResourceAsStream(fileName);
			} else {
				is = new FileInputStream(new File(fileName));
			}

			String projName = null;
			String migrationURL = null;
			String migrationPeriod = null;
			String migrationID = null;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			boolean debug = false;
			int lineNum = 1;
			while ((line = reader.readLine()) != null) {
				try {
					line = line.trim();
					if (line.equals(""))
						continue;

					String[] token = line.split(" +");

					if (token[0].equals("CREATE_AGENT")) {

						String name = token[1];

						Role role = new Role(token[2], loader,
								new ArrayList<String>(), new HashSet<String>(),
								new HashSet<String>(), new HashSet<Module>(),
								moduleMap);
						String sleepTime;
						if (token.length > 2) {
							sleepTime = token[3];
						} else
							sleepTime = "1021";
						agents.add(new Agent(name, role, sleepTime));

						if (!roles.contains(role)) {
							roles.add(role);
						}
					} else if (token[0].equals("ADD_BELIEF"))
						initialBeliefs
								.add(new InitialBelief(token[1], token[2]));
					else if (token[0].equals("START_AGENT"))
						starters.add(token[1]);
					else if (token[0].equals("PROJECT_NAME"))
						projName = token[1];
					else if (token[0].equals("SERVICE")) {
						String string = token[1];
						services.add(new Service(string, token));
					} else if (token[0].equals("INTERFACE")) {
						String string = token[1];
						String period;
						if (string.equals("THREAD")) {
							period = token[2];
							gi = new GraphicalInterface(token[3], period,
									token, 4);
						} else {
							gi = new GraphicalInterface(string, "0", token, 2);
						}
					} else if (token[0].equals("MIGRATION")) {
						migrationPeriod = token[1];
						migrationURL = token[2];
						migrationID = token[3];
					} else if (token[0].equals("SCHEDULER")) {
						numThread = token[1];
					} else if (token[0].equals("MODULE")) {
						moduleMap.put(token[1], token[2]);
					} else if (token[0].equals("DEBUG"))
						debug = true;

					lineNum++;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("!!!! Error on line " + lineNum + " of "
							+ fileName);
				}

			}
			reader.close();
			String path = System.getProperty("user.dir") + "\\";
			path += fileName.substring(0, fileName.lastIndexOf('/') + 1);
			File outputLet = new File(path + "/" + projName + "Let.java");
			String pack = null;
			if (fileName.indexOf('/') > -1)
				pack = fileName.substring(0, fileName.lastIndexOf('/'))
						.replace('/', '.');

			ArrayList<String> list = processTemp(let,  projName);
			if(pack==null){
				list=processTemplate(list,"<<project>>", projName);
				for(int i=0;i<list.size();i++){
				
					if(list.get(i).indexOf("<<package>>")>-1)list.remove(i);
				}
			}
			else list = processTemplate(processTemplate(list, "<<package>>", pack),
					"<<project>>", projName);

			ArrayList<String> newList = new ArrayList<String>();

			for (String s : list) {

				if (s.contains("<<displayfunc>>")) {

					newList.add(s.replace("<<displayfunc>>", ""));

					BufferedReader br = new BufferedReader(
							new InputStreamReader(
									Compiler.class
											.getResourceAsStream((debug) ? "debug.template"
													: "display.template")));
					String str = null;
					while ((str = br.readLine()) != null)
						newList.add(str);
					br.close();
				} else
					newList.add(s);
			}

			list = newList;
			newList = new ArrayList<String>();
			removeComment(list, newList);

			createFile(outputLet, newList);

			list = processTemp(platform,  projName);
			newList = new ArrayList<String>();
			for (String s : list) {

				if (s.contains("<<declaration>>")) {

					newList.add(s.replace("<<declaration>>", ""));
					if (agents.size() > 0) {
						StringBuffer buffer = new StringBuffer(
								"    AgentRunnable ");
						boolean follow = false;

						for (Agent agent : agents) {
							if (follow)
								buffer.append(",");
							follow = true;
							agent.addName(buffer);

						}
						buffer.append(";");
						newList.add(buffer.toString());

						for (Agent agent : agents) {
							StringBuffer nameBuffer = new StringBuffer(
									"       AgentName ");
							agent.addName(nameBuffer);
							nameBuffer.append("Name;");
							newList.add(nameBuffer.toString());
						}
					}

				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<construction>>")) {

					newList.add(s.replace("<<construction>>", ""));
					// StringBuffer buffer=new StringBuffer();

					for (Agent agent : agents) {
						StringBuffer nameBuffer = new StringBuffer("        ");
						agent.addName(nameBuffer);
						nameBuffer.append("Name=createName(\"");
						agent.addName(nameBuffer);
						nameBuffer.append("\");");
						newList.add(nameBuffer.toString());

						agent.printConstruction(newList);

					}
					if (initialBeliefs.size() != 0) {
						newList.add("        try{");
						for (InitialBelief belief : initialBeliefs) {
							belief.printAddBelief(newList);
						}
						newList
								.add("         }catch(MalformedLogicException e){");
						newList.add("             e.printStackTrace();");
						newList.add("         }");
					}
				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();

			for (String s : list) {
				if (s.contains("<<roles>>")) {
					newList.add(s.replace("<<roles>>", ""));
					for (Role rl : roles) {
						rl.printConstructMethod(newList, debug);
					}
				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();

			for (String s : list) {
				if (s.contains("<<num_thread>>")) {
					newList.add(s.replace("<<num_thread>>", ""));
					newList.add("        scheduler=new Scheduler(" + numThread
							+ ");");

				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();

			for (String s : list) {
				if (s.contains("<<stringNames>>")) {

					newList.add(s.replace("<<stringNames>>", ""));
					StringBuffer nameBuffer = new StringBuffer(
							"            String[]stringNames=new String[");
					nameBuffer.append(agents.size());
					nameBuffer.append("];");
					newList.add(nameBuffer.toString());
					int i = 0;
					for (Agent agent : agents) {
						nameBuffer = new StringBuffer("            ");
						agent.addName(nameBuffer);
						nameBuffer.append("Name.addName(stringNames,");
						nameBuffer.append(i);
						nameBuffer.append(");");
						i++;
						newList.add(nameBuffer.toString());
					}

				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();

			for (String s : list) {
				if (s.contains("<<agentdisplay>>")) {
					newList.add(s.replace("<<agentdisplay>>", ""));
					int size = agents.size();
					Iterator<Agent> iter = agents.iterator();
					for (int i = 0; i < size; i++) {
						newList.add("            if(list.getSelectedIndex()=="
								+ i + "){");
						iter.next().addDisplay(newList);
						newList.add("//--                return;");
						newList.add("//--            }");
					}
				} else {
					newList.add(s);
				}

			}

			list = newList;
			newList = new ArrayList<String>();

			for (String s : list) {
				if (s.contains("<<namearray>>")) {
					newList.add(s.replace("<<namearray>>", ""));
					StringBuffer buffer = new StringBuffer(
							"        final AgentName agentName[]=new AgentName[");
					buffer.append(agents.size());
					buffer.append("];");
					newList.add(buffer.toString());
					int i = 0;
					for (Agent agent : agents) {
						StringBuffer nameBuffer = new StringBuffer(
								"         agentName[");
						nameBuffer.append(i);
						nameBuffer.append("]=");
						agent.addName(nameBuffer);
						nameBuffer.append("Name;");
						newList.add(nameBuffer.toString());
						i++;
					}
					/*
					 * Iterator<Agent>iter=agents.iterator();
					 * buffer.append("\""); iter.next().addName(buffer);
					 * buffer.append("\""); do{
					 * 
					 * buffer.append(","); buffer.append("\"");
					 * iter.next().addName(buffer); buffer.append("\"");
					 * }while(iter.hasNext()); buffer.append("};");
					 * newList.add(buffer.toString());
					 */
				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<listname>>")) {
					newList.add(s.replace("<<listname>>", ""));
					for (Agent agent : agents) {
						StringBuffer buffer = new StringBuffer(
								"        list.add(");
						agent.addName(buffer);
						buffer.append(");");
						newList.add(buffer.toString());
					}
				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<services>>")) {
					newList.add(s.replace("<<services>>", ""));
					for (Service service : services) {
						service.addConstruction(newList);
					}
				} else {
					newList.add(s);
				}
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<interface>>")) {
					if (gi != null) {
						newList.add(s.replace("<<interface>>", ""));

						gi.addConstruction(newList);
					}
				} else
					newList.add(s);
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<startagents>>")) {
					for (Agent agent : agents) {

						for (String string : starters) {
							// System.out.println("printing agent "+string);
							agent.start(newList, string);
						}
					}
				} else
					newList.add(s);
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<scheduleagents>>")) {
					for (Agent agent : agents) {
						StringBuffer buffer = new StringBuffer(
								"        scheduler.schedule(");
						agent.addName(buffer);
						buffer.append("Name,");
						agent.addName(buffer);
						buffer.append(",");
						agent.addSleepTime(buffer);
						buffer.append(");");
						newList.add(buffer.toString());
					}
				} else
					newList.add(s);
			}

			list = newList;
			newList = new ArrayList<String>();
			for (String s : list) {
				if (s.contains("<<migration_import>>")) {
					newList.add(s.replace("<<migration_import>>", ""));
					if (migrationURL != null) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(Compiler.class
										.getResourceAsStream("migimp.template")));
						String str = null;
						while ((str = br.readLine()) != null)
							newList.add(str);
						br.close();
					}
				} else if (s.contains("<<migration_platform")) {
					if (migrationURL == null)
						newList.add(s.replace("<<migration_platform>>", ""));
					else
						newList.add(s.replace("<<migration_platform>>",
								",MigrationPlatform"));

				} else if (s.contains("<<migration_imp>>")) {
					if (migrationURL == null)
						newList.add(s.replace("<<migration_imp>>", ""));
					else
						newList.add(s.replace("<<migration_imp>>",
								"implements MigrationPlatform"));

				} else if (s.contains("<<migration_declare>>")) {
					newList.add(s.replace("<<migration_declare>>", ""));
					/*
					 * if (migrationURL != null) { newList.add("
					 * MigrationManager migrationManager;"); }
					 */
				} else if (s.contains("<<migration_construct>>")) {
					newList.add(s.replace("<<migration_construct>>", ""));
					if (migrationURL != null) {
						newList.add("       new MigrationManager(\""
								+ migrationURL + "\",this,\"" + migrationID
								+ "\",scheduler," + migrationPeriod
								+ ").register(services);");
					}
				} else if (s.contains("<<migration_methods>>")) {
					newList.add(s.replace("<<migration_methods>>", ""));
					if (migrationURL != null) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(
										Compiler.class
												.getResourceAsStream("migmethod.template")));
						String str = null;
						while ((str = br.readLine()) != null)
							newList.add(str);
						br.close();
					}
				} else
					newList.add(s);
			}

			list = newList;
			newList = new ArrayList<String>();

			//removeComment(list, newList);
			if(pack==null){
				newList=processTemplate(list,"<<project>>", projName);
				for(int i=0;i<newList.size();i++){
					
					if(newList.get(i).indexOf("<<package>>")>-1)newList.remove(i);
				}
			}
			else newList = processTemplate(processTemplate(list, "<<package>>",
					pack), "<<project>>", projName);
			File outputPlatform = new File(path + '/' + projName
					+ "AgentPlatform.java");
			
			list = newList;
			newList = new ArrayList<String>();

			removeComment(list, newList);
			
			createFile(outputPlatform, newList);

		} catch (Exception e) {
			System.err.println("Error in CLDC config file, could not compile");
			System.err.println(fileName);
			e.printStackTrace();
		}

	}

	private static ArrayList<String> processTemp(String fileName,
			String projName) {
		try {
			ArrayList<String> list = new ArrayList<String>();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemClassLoader().getResourceAsStream(
							fileName)));
			String string;
			while ((string = br.readLine()) != null) {
				list.add(string);
			}
			br.close();
			return list;

		} catch (Exception e) {
			System.err.println("Error " + e.toString());
		}
		return null;

	}

	private static void removeComment(ArrayList<String> list,
			ArrayList<String> newList) {
		for (String str : list) {
			if (str.trim().startsWith("##"))
				continue;
			newList.add(str);
		}
	}

	private static void createFile(File fileName, ArrayList<String> list) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));
			for (String s : list) {
				writer.println(s);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println("Error " + e.toString());
		}

	}

	private static ArrayList<String> processTemplate(ArrayList<String> list,
			String key, String replacement) {
		String star = key + "*";
		ArrayList<String> newList = new ArrayList<String>();
		for (String s : list) {
			if (s.contains(star)) {
				newList.add(s.replace(star, replacement));
			} else if (s.contains(key)) {
				newList.add(s.replace(key, replacement));
			} else {
				newList.add(s);
			}
		}
		return newList;
	}

}
