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

package com.agentfactory.cldc.scheduler;
import java.io.DataOutput;

import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.logic.FOS;

/** This is an abstract that is used to control the process of executing an
 * agent in the scheduler. In MIDP, the MIDletRunnable will be used for debugging
 * purposes, but in environments where there is no interface BasicRunnable will be used or
 * some other extensions of AgentRunnable that the application developer 
 * has written.
 * @author Conor Muldoon
 *
 */
public abstract class AgentRunnable implements Runnable{
	Agent agent;
	/**Constructs an instance of AgentRunnable.
	 * @param agt the agent that the runnable class controls.
	 */
	public AgentRunnable(Agent agt){
		agent=agt;
		
		
	}
	
	/**
	 * Puts the agent into a torpor state.
	 *
	 */
	public abstract void torpor();
	
    /** Wakes an agent up from a torpor state.
     */
	public abstract void wake();
	
	/** Adds a belief to the agent.
	 * @param fos the belief to be added.
	 */
	public void addFOSBelief(FOS fos){
		
		agent.addFOSBelief(fos);
	}
	
	/** Starts the agent.
	 * 
	 */
	public abstract void start();
	
	/** Displays the mental state of the agent.
	 * 
	 */
	public abstract void display();
	
	/** Stops the agent.
	 * 
	 */
	public abstract void stop();
	
	/** Writes the state of the agent to a data output stream.
	 * @param output the stream that the state is to be written to.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	public void writeToStream(DataOutput output)throws java.io.IOException{
		agent.writeToStream(output);
		
	}

}
