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
package com.agentfactory.cldc;

import java.util.Hashtable;

import com.agentfactory.cldc.logic.Action;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;

/** In AFME, agents act on their environment through the use of Actions. Actuators
 * extend the Action class. The Actuator class includes methods to enable agents to adopt and
 * retract beliefs. It stores an Actuator's identifier or trigger, which is
 * used within a commitment rule. It also contains a reference to the 
 * AffectManager, which is used to enable the Actuator to interact with
 * modules and services.
 * @author Conor Muldoon
 *
 */
public abstract class Actuator extends Action {

	private FOS identifier;

	private AffectManager manager;



	/** Constructs an actuator that enables an agent to interact with its environment
	 * @param manager the affect manager for the agent.
	 * @param id a string representation of the trigger for the actuator.
	 */
	public Actuator(AffectManager manager, String id) {
		this.manager = manager;

		try {
			identifier = FOS.createFOS(id);
		} catch (MalformedLogicException e) {
			e.printStackTrace();
		}
	}

	/** This method is called by the agent platform to register the actuator with the platform. 
	 * The identifier specified in the constructor is used to identify the actuator when a particular action is required.
	 * @param table the hash table that the actuator is added to.
	 */
	public void register(Hashtable table) {
		identifier.addToTable(table, this);
		
	}

	/** Causes the agent to adopt the specified FOS belief.
	 * This method differs
	 * from {@link com.agentfactory.cldc.Perceptor#adoptBelief(FOS) adoptBelief(FOS)}.
	 *  The belief is not added to
	 * the belief set during the current cycle. The reason for this is that commitment
	 * execution is nondeterministic. That is, the order in which commitments
	 * are fired in the current cycle is irrelevant at a logical level
	 *  and will not have a consequence on the resolution-based reasoning
	 *  process for determining the desired states in the current cycle. Beliefs
	 *  added using this method will only be added to the agents current belief
	 *  set after all of the commitments in the current cycle have been executed.
	 * @param bel the belief to be adopted.
	 */
	public void adoptBelief(FOS bel) {
		manager.adoptBelief(bel);
	}

	/** Causes the agent to adopt the belief represented by the specified string.
	 * @param string the string representation of the belief to be adopted.
	 */
	public void adoptBelief(String string) {
		try {
			manager.adoptBelief(FOS.createFOS(string));
		} catch (MalformedLogicException e) {
			e.printStackTrace();
		}
	}

	/** Causes the agent to drop the specified FOS belief.
	 * It operates in 
	 * a similar manner to {@link #adoptBelief(FOS) adoptBelief(FOS)}.
	 * The belief is not retracted until the current control process
	 * has finished determining the desired states. If the agent does not
	 * contain the specified belief, no error is thrown.
	 * 
	 * @param bel the belief to be dropped.
	 */
	public void retractBelief(FOS bel) {
		manager.retractBelief(bel);
	}

}
