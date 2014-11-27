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

/* author: Conor Muldoon */
package com.agentfactory.cldc;



import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.scheduler.AgentRunnable;

/** This class provides basic functionality for executing an agent. It is
 * used in environments where there is no GUI functionality (such as the
 * Stargate WSN gateway node), therefore
 * is does not provide a GUI debugging interface. 
 * 
 * @author Conor Muldoon
 *
 */
public class BasicRunnable extends AgentRunnable {


	private Agent agent;

	boolean awake;
	
	/** Creates a new instance of BasicRunnable.
	 * 
	 * @param a the agent that is to be executed.
	 */
	public BasicRunnable(Agent a) {
		super(a);
		agent = a;
		awake=true;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#wake()
	 */
	public void wake(){
		awake=true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#torpor()
	 */
	public void torpor(){
		awake=false;
	}

	/** This method is left blank in the BasicRunnable class since there
	 * is no GUI for starting or stopping an agent.
	 * 
	 */
	public void start() {
	
	}
	/** This method is left blank in the BasicRunnable class since there
	 * is no GUI for starting or stopping an agent.
	 * 
	 */
	public void stop() {
		
	}

	/** This method is left blank in the BasicRunnable since there is no
	 * GUI functionality provided.
	 * 
	 */
	public void display() {

		

	}


	/** This method updates the agent's beliefs and then steps one
	 * cycle of the agent's control process.
	 */
	synchronized public void run() {
		if(awake){
		agent.updateBeliefs();
		agent.step();
	
		}
	}

}
