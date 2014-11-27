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
package com.agentfactory.cldc.logic;

import java.util.Calendar;
import java.util.Hashtable;

/** In AFME, behaviour is modelled through the use of beliefs and commitments.
 * The commitment class contains the functionality for managing the commitment
 * life cycle process.
 * 
 * @author Conor Muldoon
 */
public class Commitment {

	FOS agentCommitedTo;

	Calendar startTime;

	FOS maintenanceCondition;

	FOS activity;

	Commitment parent;

	private byte state = STATE_NEW;

	Commitment[] children;

	private static final byte SIZE = 2;

	private int size, count;

	static final byte STATE_NEW = 0;

	static final byte STATE_WAITING = 1;

	static final byte STATE_FAILED = 2;

	static final byte STATE_SUCCEEDED = 3;

	static final byte STATE_REDUNDANT = 4;

	static final byte PAR = 1;

	static final byte SEQ = 2;

	static final byte OR = 3;

	static final byte XOR = 4;

	private byte act;

	int v, c;
	FOS id;

	/** Creates an instance of Commitment.
	 * 
	 * @param inAgent the agent that the commitment belongs to.
	 * @param inTime the time before which the commitment will not be executed.
	 * @param maintenance the maintenance condition of the commitment.
	 * @param inActivity the activity (action or plan) of the commitment.
	 * @param inParent the commitment's parent commitment.
	 * @param val the value of the commitment.
	 * @param cost the cost of the commitment.
	 * @param ident the identifier for the commitment.
	 */
	public Commitment(FOS inAgent, Calendar inTime, FOS maintenance,
			FOS inActivity, Commitment inParent, FOS val, FOS cost,FOS ident) {
		
		parent = inParent;
		agentCommitedTo = inAgent;
		startTime = inTime;
		maintenanceCondition = maintenance;
		activity = inActivity;
		
		updateValues(val,cost);
		id=ident;
		
		children = new Commitment[SIZE];
		if (activity.functorEquals("par")) {
			act = PAR;
			return;
		}
		if (activity.functorEquals("seq")) {
			act = SEQ;
			return;
		}
		if (activity.functorEquals("or")) {
			act = OR;
			return;
		}
		if (activity.functorEquals("xor")) {
			act = XOR;

		}
		
		
		
	}
	
	/** Adds the commitment to the table of commitments
	 * that are to have their values and costs altered.
	 * The identifier of the commitment is used as the
	 * key in the hash table.
	 * 
	 * @param agent the agent that has made the commitment.
	 */
	public void variableVals(Agent agent){
		
		if(id!=null){
			
			agent.addToMod(id.toString(),this);
		}
	}
	
	/** Removes the commitment from the table of commitments
	 * that are to have their values and costs altered.
	 * 
	 * @param agent the agent that has made the commitment.
	 */
	public void removeVarVals(Agent agent){
		if(id!=null)
			agent.removeFromMod(id.toString());
	}
	
	/** Updates the values and cost of the commitment.
	 * 
	 * @param val the new value of the commitment.
	 * @param cost the new cost of the commitment.
	 */
	public void updateValues(FOS val, FOS cost){
		String vl = val.toString();
		String ct = cost.toString();
		v = Integer.parseInt(vl);
		c = Integer.parseInt(ct);
		
	}

	/** Adds the value and cost of the commitment to the 
	 * specified arrays and the specified index.
	 * 
	 * @param val the value array.
	 * @param cost the cost array.
	 * @param i the index of the value and cost array to update.
	 */
	public void addValues(int[] val, int[] cost, int i) {

		val[i] = v;
		cost[i] = c;

	}


	private boolean processActivity(Agent agent, Hashtable actuators)
			throws MalformedLogicException {

		// System.out.println("processing commitment");
		switch (act) {
		case PAR:
		case OR:

			addChild(agent);
			state = STATE_WAITING;
			return true;

		case SEQ:
			procState(agent, true);
			return true;

		case XOR:
			procState(agent, false);
			return true;

		default:

			Action actuator = (Action) activity.getFromTable(actuators);

			activity.reset();
			if (actuator == null) {
				System.err.println("No Action for: " + activity);
				return true;
			}
			boolean bool = actuator.act(activity);

			if (bool){
				state = STATE_SUCCEEDED;
				c=0;
			}else
				state = STATE_FAILED;
			notifyPar(bool);
			return bool;

		}

	}

	void notifyPar(boolean bool) {
		if (parent == null)
			return;
		if (parent.state == STATE_WAITING) {
			if (bool)
				parent.childSuccess();
			else
				parent.childFail();
		}
	}

	private void procState(Agent agent, boolean success)
			throws MalformedLogicException {
		if (state == STATE_NEW) {
			if (activity.hasNext()) {
				addChild(agent, activity.next());
				state = STATE_WAITING;
			} else {
				if (success)
					state = STATE_SUCCEEDED;
				else
					state = STATE_FAILED;
			}
		}

	}


	boolean maintenance(Agent agent)
			throws MalformedLogicException {

		if (maintenanceCondition.isNot()) {
			FOS fos = maintenanceCondition.invert();

			if (fos.isTrue() || agent.processFOS(fos, null))
				return false;
		} else if(maintenanceCondition.isExpression()){
			// to do:
		}else if (!(maintenanceCondition.isTrue() || agent.processFOS(
				maintenanceCondition, null)))
			return false;
		return true;

	}

	/** Manages the current commitment state. If the commitment is a direct
	 * action, the action will be performed provided that the maintenance 
	 * condition is true. If the commitment is a plan, the plan state will
	 * be updated.
	 * 
	 * @param agent the agent that has made the commitment.
	 * @param actuators the hash table that matches actuators to their triggers.
	 * @return false if the commitment is completed, is redundant, or is to be
	 * removed, true otherwise.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public boolean process(Agent agent, Hashtable actuators)
			throws MalformedLogicException {

		switch (state) {
		case STATE_WAITING:
			
			return maintenance(agent);
		case STATE_NEW:

			if (maintenance(agent)) {

				if (Calendar.getInstance().before(startTime)){
					//System.out.println(Calendar.getInstance().getTime().toString()+" ___ "+startTime.getTime().toString());
					return true;
				}
				return processActivity(agent, actuators);
			}

			return false;

		case STATE_FAILED:
			notifyPar(false);

			StringBuffer buffer = new StringBuffer("commitment_failed(");
			activity.append(buffer);
			buffer.append(')');
			agent.addFOSBelief(FOS.createFOS(buffer.toString()));
			return false;
		case STATE_SUCCEEDED:
			notifyPar(true);
		default:

			// STATE_REDUNDANT or SUCCEEDED
			agent.retractBelief(toCommitBelief());
			for (int i = size; i-- > 0;) {
				if (children[i].state == STATE_WAITING
						|| children[i].state == STATE_NEW)
					children[i].state = STATE_REDUNDANT;
			}
			
			return false;
		}

	}

	/** For each child commitment of this commitment, add a child commitment
	 * to the agent.
	 * 
	 * @param agent the agent that has made the commitment.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void addChild(Agent agent) throws MalformedLogicException {
		
		for (FOS f = activity.next(); f != null; f = activity.next()){
				addChild(agent, f);
		}
	}

	/** Converts the commitment to a commitment belief. The belief takes the
	 * form always(committed_to(?activity)).
	 * 
	 * @return the commitment belief.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public FOS toCommitBelief() throws MalformedLogicException {
		StringBuffer buffer = new StringBuffer("always(committed_to(");
		activity.append(buffer);
		buffer.append("))");
		return FOS.createFOS(buffer.toString());
	}

	void addChild(Agent agent, FOS fos) throws MalformedLogicException {
		
		Commitment newCommitment = new Commitment(agentCommitedTo, startTime,
				FOS.createFOS("true"), fos, this, FOS.createFOS("1"), FOS
						.createFOS("0"),null);
		
		if (size == children.length) {
			Commitment[] newArray = new Commitment[children.length << 1];
			System.arraycopy(children, 0, newArray, 0, size);
			children = newArray;
		}
		
		children[size] = newCommitment;
		size++;
		
		agent.adoptChild(newCommitment);
		
		

	}

	private void childFail() {
		count++;
		if (act == OR) {
			if (size > count)
				return;
		} else if (act == XOR) {
			state = STATE_NEW;
			return;
		}
		state = STATE_FAILED;
	}

	private void childSuccess() {
		count++;
		if (act == PAR) {
			if (size > count)
				return;
		} else if (act == SEQ) {
			state = STATE_NEW;
			return;
		}
		state = STATE_SUCCEEDED;

	}

	/** Appends the commitment to the specified string buffer.
	 * 
	 * @param buffer the string buffer the commitment will be appended to.
	 */
	public void append(StringBuffer buffer) {
		int minuteBit = startTime.get(Calendar.MINUTE);
		buffer.append("commit(");
		agentCommitedTo.append(buffer);
		buffer.append(',');
		buffer.append(startTime.get(Calendar.HOUR_OF_DAY));
		buffer.append('.');
		buffer.append((minuteBit < 10) ? "0" : "");
		buffer.append(minuteBit);
		buffer.append(',');
		maintenanceCondition.append(buffer);
		buffer.append(',');
		activity.append(buffer);
		buffer.append(',');
		buffer.append((int) state);
		buffer.append(')');
	}

}
