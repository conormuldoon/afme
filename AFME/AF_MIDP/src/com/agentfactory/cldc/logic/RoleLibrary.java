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
 * RoleLibrary.java
 *
 * Created on 08 August 2005, 17:21
 */

package com.agentfactory.cldc.logic;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/** The role library contains the role templates that the agent
 * will instantiate under various circumstances. 
 * In AFME, each agent may have several role templates. Additionally,
 * each template may have several instances. The role library contains and
 * manages the hash table of role templates and the hash table of role instances.
 * 
 * @author Conor Muldoon
 */
public class RoleLibrary {

	Hashtable template;

	Hashtable instance;

	/** Creates a new instance of RoleLibrary. */
	public RoleLibrary() {
		template = new Hashtable();
		instance = new Hashtable();
	}
	
	/** Adds a role template to the role template table using the functor of
	 * the  specified identifier as the key.
	 * 
	 * @param identifier the identifier for the template.
	 * @param rt the role template to be added to the role template table.
	 */
	public void add(FOS identifier,RoleTemplate rt){
		identifier.addToTable(template,rt);
	}
	
	/** If there is not already a role instance with the specified identifier,
	 * creates a role instance using the appropriate template.
	 * 
	 * @param identifier the identifier for the role.
	 
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void enactRole(FOS identifier)
			throws MalformedLogicException {
		if (instance.containsKey(identifier.toString()))
			return;
		
		RoleTemplate temp = (RoleTemplate) identifier.getFromTable(template);
		temp.instantiate(identifier, instance);
	}

	/** Deactivates the role that has the specified identifier.
	 * 
	 * @param fos the identifier of the role that is to be deactivated.
	 */
	public void deactivateRole(FOS fos) {
		//System.out.println("removing "+fos);
		instance.remove(fos.toString());
		
	}

	/** Writes each of the role templates to the the specified
	 * data output stream.
	 * 
	 * @param output the stream that the templates are written to.
	 * @throws IOException if there is an I/O error.
	 */
	public void writeToStream(DataOutput output) throws IOException {
		output.writeInt(template.size());
		Enumeration e = template.elements();
		while (e.hasMoreElements())
			((RoleTemplate) e.nextElement()).writeToStream(output);

	}

	/** Processes the triggers of each of the role templates and executes
	 * each of the role instances.
	 * 
	 * @param agent the agent that the role library belongs to.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void solve(Agent agent) throws MalformedLogicException {
		//System.out.println("solving");
		Enumeration e = template.elements();
		while (e.hasMoreElements())
			((RoleTemplate) e.nextElement()).processTrigger(agent, instance);

		e = instance.elements();
		
		while (e.hasMoreElements()) {

			agent.solveRule((TerImplication[]) e.nextElement());
			

		}
	}

	/** Appends the role instances to the specified buffer.
	 * 
	 * @param buffer the string buffer that role instances are appended to.
	 */
	public void append(StringBuffer buffer) {
		Enumeration e = instance.elements();

		while (e.hasMoreElements()) {
			TerImplication[] rule = (TerImplication[]) e.nextElement();
			//System.out.println(rule);
			for (int i = rule.length; i-- > 0;) {
				rule[i].append(buffer);
			}
		}
	}

}
