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
 * Role.java
 *
 * Created on 26 January 2005, 14:25
 */

package com.agentfactory.cldc.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Map;

/**
 * 
 * @author Conor Muldoon
 */
public class Role {

	private static final String COMMENT = "/**";

	private static final String PERCEPTOR = "PERCEPTOR";

	private static final String ACTUATOR = "ACTUATOR";

	private static final String SERVICE = "SERVICE_BIND";

	private static final String MODULE = "LOAD_MODULE";

	private static final String ROLE = "USE_ROLE";

	private static final String BELIEF = "BELIEF";

	private static final String NOT = "!";

	private static final String NEW_ROLE = "ROLE";

	private static final String TRIGGER = "TRIGGER";

	String constructName;

	Collection<String> design;

	Collection<String> actuators;

	Collection<String> perceptors;

	Collection<Module> modules;

	Collection<NewRole> newRole;

	/** Creates a new instance of Role */
	public Role(String fileName, URLClassLoader loader,
			Collection<String> design, Collection<String> actuators,
			Collection<String> perceptors, Collection<Module> modules,
			Map<String, String> mm) {
		this.design = design;
		this.perceptors = perceptors;
		this.actuators = actuators;
		this.modules = modules;

		StringTokenizer tk = new StringTokenizer(fileName, ".");
		constructName = tk.nextToken();

		constructName = constructName.replace('/', '_');
		InputStream is;
		try {
			if (loader == null) {
				try {
					is = new FileInputStream(new File(fileName));
				} catch (NullPointerException e) {
					System.out.println("Problem with: " + fileName);
					throw e;
				}
			} else {
				
					// System.out.println("file name "+fileName);
				System.out.println("Getting Resource As Stream "+fileName);	
				is = loader.getResourceAsStream(fileName);
				
			}
			// if(loader==null)System.out.println("loader null");
			// else System.out.println("not null");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			newRole = new ArrayList<NewRole>();
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0)
					continue;
				StringTokenizer tok = new StringTokenizer(line);
				String token = tok.nextToken().trim();
				if (token.equals(COMMENT) || tok.equals(SERVICE))
					continue;
				if (token.equals(PERCEPTOR)) {
					String per = tok.nextToken();
					perceptors.add(per.substring(0, per.length() - 1));
					continue;
				}
				if (token.equals(ACTUATOR)) {
					String act = tok.nextToken();
					actuators.add(act.substring(0, act.length() - 1));
					continue;
				}
				if (token.equals(MODULE)) {
					String name = tok.nextToken();
					String className = tok.nextToken();
					modules.add(new Module(name, className.substring(0,
							className.length() - 1), mm));
					continue;
				}
				if (token.equals(ROLE)) {
					String roleName = tok.nextToken().replace('.', '/');
					roleName = roleName.substring(0, roleName.length() - 1)
							+ ".rle";
					// Role role=new
					// Role(roleName,loader,design,actuators,perceptors,modules);
					continue;
				}
				if (token.equals(NEW_ROLE)) {
					String id = tok.nextToken();
					Collection<String> rule = new ArrayList<String>();
					Collection<String> trigger = new ArrayList<String>();
					while (true) {
						line = reader.readLine();
						if (line.indexOf("}") != -1)
							break;
						if (line.indexOf("{") != -1)
							continue;
						// assuming no actuators or perceptors in roles
						if (line.startsWith(TRIGGER)) {
							String[] s = line.split(" ");
							trigger.add(s[1].substring(0, s[1].length() - 1));
						}
						if (line.startsWith(BELIEF) || line.startsWith(NOT))
							rule.add(line);

					}
					newRole.add(new NewRole(id, rule, trigger));
					continue;
				}
				if (token.startsWith(BELIEF) || line.startsWith(NOT)) {
					// System.out.println(line);

					design.add(line);
				}

			}
		} catch (Exception e) {
			System.out.println("Error in Role File " + fileName);
			e.printStackTrace();
		}

	}

	public void printConstructMethod(ArrayList<String> list, boolean b) {
		list.add("    private MIDletRunnable " + constructName
				+ "(AgentName name){");
		list.add("        Perceptor[]perceptors=new Perceptor["
				+ perceptors.size() + "];");
		list.add("        Hashtable actuators=new Hashtable();");

		list.add("        RoleLibrary roleLib=new RoleLibrary();");
		for (NewRole nr : newRole)
			nr.constructTemplate(list);

		list.add("        Hashtable modules=new Hashtable();");

		for (Module module : modules) {
			module.register(list);
		}

		list.add("        TerImplication[]mentalState=new TerImplication["
				+ design.size() + "];");
		list.add("        try{");
		Object[] array = design.toArray();
		for (int j = array.length; --j >= 0;) {
			list.add("            mentalState[" + j + "]=new TerImplication(\""
					+ array[j] + "\");");
		}
		list.add("        }catch(Exception e){");
		list.add("            e.printStackTrace();");
		list.add("        }");

		list
				.add("              Agent agent=new Agent(mentalState,actuators,perceptors,roleLib);");
		// list.add(" /*#!Debug_midp1_0_cldc1_0#*///<editor-fold>");
		// list.add(" Agent agent=new
		// Agent(mentalState,actuators,perceptors,roleLib);");
		// list.add(" /*$!Debug_midp1_0_cldc1_0$*///</editor-fold>");
		list
				.add("        PerceptionManager perManager=new PerceptionManager(agent,name,modules,services,scheduler);");
		list
				.add("        AffectManager affManager=new AffectManager(agent,name,modules,services,scheduler,roleLib);");

		array = perceptors.toArray();
		for (int j = array.length; --j >= 0;) {
			list.add("        perceptors[" + j + "]=new " + array[j]
					+ "(perManager);");
		}

		for (String actuator : actuators)
			list.add("        new " + actuator
					+ "(affManager).register(actuators);");

		// fix me for generic runnable implementation
		String s = (b) ? "let" : "null";
		list.add("		  return new MIDletRunnable(agent," + s
				+ ",mentalState,roleLib,list);");

		list.add("    }");

	}

	public String addConstructName(String string) {
		return string + constructName;
	}

	public boolean equals(Object obj) {

		return constructName.equals(((Role) obj).constructName);
	}

}
