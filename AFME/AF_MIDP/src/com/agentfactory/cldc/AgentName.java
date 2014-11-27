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
 * AgentName.java
 *
 * Created on 14 April 2005, 11:37
 */

package com.agentfactory.cldc;

import java.util.Hashtable;

import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.scheduler.AgentRunnable;

/** This class represents the name of an agent. It provides support for
 * the name to be updated to a unique value and for checking whether the
 * name matches a wild card representation.
 * 
 * @author Conor Muldoon
 */
public class AgentName {
	FOS name;

	public final static char DELIM = '$';

	String app;
	Platform p;

	/** Creates a new instance of AgentName
	 * 
	 * @param nm a FOS representation of the agent name.
	 * @param appVal the application name.
	 * @param plat the platform the agent is operating on.
	 */
	public AgentName(FOS nm, String appVal,Platform plat) {
		name = nm;
		app = appVal;
		p=plat;
	}

	/** Creates a new instance of AgentName
	 * @param s A string representation of the agent name.
	 * @param a the application name.
	 */
	public AgentName(String s, String a) {
		try {
			name = FOS.createFOS(s);
			app = a;
		} catch (MalformedLogicException e) {
			e.printStackTrace();
		}
	}

	

	/** Tests whether this is a unique name. A name is considered unique if 
	 * it has been obtained from the unique name server, therefore if the name
	 * is unique on the local platform but has not been obtained from the unique
	 * name server, this method will return false (even though it will be unique
	 * locally).
	 * @return true if the name is unique, false otherwise.
	 */
	public boolean isUnique() {
		return name.containsChar(DELIM);
	}

	/** Adds a string representation of the name to the array at the 
	 * specified index.
	 * @param array the array the name is to be added to.
	 * @param i the index at which the name should be added.
	 */
	public void addName(String[] array, int i) {
		array[i] = name.toString();
	}


	/** The hash code of the name.
	 * 
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/** Adds the AgentRunnable to the hash table using the functor
	 * of the FOS representation of the agent name
	 * as the key.
	 * @param table the table the name is to be added.
	 * @param agent the AgentRunnable that is to be added.
	 */
	public void addToTable(Hashtable table, AgentRunnable agent) {
		name.addToTable(table, agent);
	}
	
	/** Obtains an object from the hash table using the functor of
	 * the FOS representation of the agent name as the 
	 * key.
	 * 
	 * @param table the table the object is to be obtained from.
	 * @return the object that maps to the functor.
	 */
	public Object getFromTable(Hashtable table){
		return name.getFromTable(table);
	}

	/** Writes the name to a data output stream.
	 * @param out the stream to write the name to.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	public void write(java.io.DataOutput out) throws java.io.IOException {
		out.writeUTF(name.toString());
	}

	/*
	 * public String toString(){ return name.toString(); }
	 */

	/** Appends the name to the specified string buffer.
	 * @param buffer the string buffer the name is to be appended.
	 */
	public void appendName(StringBuffer buffer) {
		name.append(buffer);
	}

	/** Tests whether this name contains the wild card symbol *.
	 * @return true if the name contains the character *, false otherwise.
	 */
	public boolean isWild(){
		return name.toString().indexOf('*')>-1;
	}
	/** Checks whether this agent name matches the specified agent name
	 * that contains a wild card. For example, if the agent name was Alice
	 * and the wild card was Al*, this method would return true. It would
	 * also return true for Alf. It would not return true for Bob.
	 * @param an the wild card name that this name is being compared to.
	 * @return true if the this name matches the wild card name, false otherwise.
	 */
	public boolean wildEquals(AgentName an) {
		String s1=name.toString();
		String s2=an.name.toString();
		int ind=s2.indexOf('*');
		return s1.startsWith(s2.substring(0,ind))&&s1.endsWith(s2.substring(ind+1));
	}


	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {

		if (o instanceof AgentName) {
			AgentName an = (AgentName) o;
			return name.equals(an.name);
		} else if (o instanceof String) {
			return name.functorEquals(o);
		}
		return false;
	}

	/** Updates the name with a unique value.
	 * @param fos a FOS representation of the unique value.
	 */
	public void update(FOS fos) {
		
		StringBuffer buffer = new StringBuffer();
		name.append(buffer);
		buffer.append(DELIM);
		fos.append(buffer);
		name = FOS.createFOS(buffer.toString());
		p.storeName(name);
	}

}
