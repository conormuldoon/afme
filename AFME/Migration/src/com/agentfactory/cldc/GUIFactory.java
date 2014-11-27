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
 * GUIFactory.java
 *
 * Created on 28 June 2005, 14:08
 */

package com.agentfactory.cldc;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

/** Creates an new instance of a module GUI. It should be noted that factories
 * are only required if migration is being used.
 *
 * @author Conor Muldoon
 */
public interface GUIFactory {

	/** Constructs a GUI module from the specified objects.
	 * 
	 * @param name the name of the agent.
	 * @param let the MIDlet for the application.
	 * @param list a list of agent names.
	 * @return a new GUI instance.
	 */
    public Module createGUI(AgentName name,MIDlet let,List list);
}
