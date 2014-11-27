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

package com.agentfactory.cldc.logic;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;


/** The Agent class includes the actuators, perceptors, commitment rules, resource constraints,
 * and roles that represent an AFME agent. It contains the core control algorithm
 * that governs agent behaviour. In AFME, agents follow a sense, deliberate, act cycle.
 * The agent's perceptors are initially fired. The desires are then identified 
 * through the use of resolution-based reasoning. The intentions are then
 * identified in the knapsack procedure. Finally, depending on the nature 
 * and degree of the commitments adopted, various actuators are fired.
 * @author Conor Muldoon
 *
 */
public class Agent {

	private static final String NEXT = "next";

	private static final String ALWAYS = "always";

	// private static final String BELIEF = "BELIEF";

	private final static byte ARRAY_SIZE = 16;
	private final static byte UV_SIZE=2;

	private int alwaysSize, nextSize, commitSize, actSize, retSize;

	private Hashtable current, actuators;

	private Perceivable[] perceptors;

	private FOS[] alwaysBeliefs, actBelief, retract, nextBeliefs;

	private TerImplication[] commitmentRules;

	private Commitment[] commitments;

	private RoleLibrary roleLib;
	
	int resources;
	
	boolean[]commitFire;
	Hashtable tempMod;
	UpdateVal[]uv;
	int uvSize;
	
	Hashtable modules;
	/** Constructs an agent from a combination of its actuators, perceptors,
	 * commitment rules, roles, and the amount of resources available to the agent.
	 * 
	 * @param commitmentRules the set of rules that are used to identify the agent's
	 * desired states.
	 * @param actuators a hash table that maps action identifiers to concrete Actions.
	 * Actions typically take the form of Actuators.
	 * @param perceptors the set of perceptors (software sensors) that are
	 * fired on each iteration of the agent's control algorithm.
	 * @param mods a hash table of modules that belong to the agent.
	 * @param lib a library of roles that the agent adopts under various circumstances.
	 * @param res the amount or resources available to the agent.
	 */
	public Agent(TerImplication[] commitmentRules, Hashtable actuators,
			Perceivable[] perceptors, Hashtable mods,RoleLibrary lib, int res) {
		resources = res;
		alwaysBeliefs = new FOS[ARRAY_SIZE];
		nextBeliefs = new FOS[ARRAY_SIZE];
		actBelief = new FOS[ARRAY_SIZE];
		retract = new FOS[ARRAY_SIZE];
		uv=new UpdateVal[UV_SIZE];
		current = new Hashtable();
		roleLib = lib;
		this.commitmentRules = commitmentRules;

		commitments = new Commitment[ARRAY_SIZE];
		this.perceptors = perceptors;
		this.actuators = actuators;
		
		tempMod=new Hashtable();
		modules=mods;

	}

	/** Updates the amount of resources available to the agent.
	 * 
	 * @param fos a FOS representation of the amount of resources available to the agent.
	 * 
	 */
	public void updateResources(FOS fos) {
		try {
			resources = Integer.parseInt(fos.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	class Node {
		private Node next;

		private FOS fos;

		Node(FOS fs) {
			fos = fs;
		}

	
	}
	
	/** Associates a commitment with a label. This method is used to 
	 * associate a label with a commitment so as that it can 
	 * be referenced at a later stage to have its associated value
	 * or cost updated.
	 * 
	 * @param s the label of the commitment.
	 * @param c the commitment to be labelled.
	 */
	public void addToMod(String s,Commitment c){
		
		tempMod.put(s,c);
	}
	
	/** This method removes the commitment that is associated with
	 * the specified label from the update table( See {@link #addToMod(String, Commitment) addToMod(String, Commitment)}).
	 * 
	 * @param s the label of the commitment.
	 */
	public void removeFromMod(String s){
		
		tempMod.remove(s);
	}
	private class UpdateVal{
		FOS v,c;
		String id;
		
		public UpdateVal(FOS val,FOS ct,String ident){
			v=val;
			c=ct;
			id=ident;
		}
	}
	/** Updates the cost and value of the commitment that has the specified 
	 * label.
	 * 
	 * @param value the new value of the commitment.
	 * @param cost the new cost of the commitment.
	 * @param id the label of the commitment.
	 */
	public void updateValues(FOS value,FOS cost,String id){
		if(uvSize==uv.length){
			UpdateVal[]temp=new UpdateVal[uvSize<<1];
			System.arraycopy(uv,0,temp,0,uvSize);
		}
		uv[uvSize++]=new UpdateVal(value,cost,id);
		
	}
	/** Writes the agent's internal state to a data output stream. This method
	 * is typically used in the migration service, but it could be conceivable 
	 * used for other purposes. For instance, it could be used to write the state to a data store for
	 * persistent execution over multiple runs.
	 * @param dos the data output stream that the state is written to.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	public synchronized void writeToStream(java.io.DataOutput dos)
			throws java.io.IOException {
		
		dos.writeInt(perceptors.length);
		dos.writeInt(resources);
		
		int len = commitmentRules.length;
		dos.writeInt(len);

		for (int i = 0; i < len; i++) {
			StringBuffer buffer = new StringBuffer();
			commitmentRules[i].append(buffer);
			// buffer.deleteCharAt(buffer.length()- 1);
			dos.writeUTF(buffer.toString());
			//System.out.println("sending "+buffer.toString());
		}
	
		
		
		for(int i=perceptors.length;i-->0;)
			dos.writeUTF(perceptors[i].getClass().toString());
		
		writeTable(actuators,dos);
		writeTable(modules,dos);

		dos.writeInt(nextSize + alwaysSize+actSize);
		for (int i = nextSize; i-- > 0;)
			dos.writeUTF(nextBeliefs[i].toString());
		for (int i = alwaysSize; i-- > 0;)
			dos.writeUTF(alwaysBeliefs[i].toString());
		for(int i=actSize;i-->0;)
			dos.writeUTF(actBelief[i].toString());
		dos.writeInt(retSize);
		for (int i = retSize; i-- > 0;){
			dos.writeUTF(retract[i].toString());
			//System.out.println("writing "+retract[i].toString());
		}

		roleLib.writeToStream(dos);

	}

	private void writeTable(Hashtable t,DataOutput dos)throws IOException{
		Enumeration e=t.elements();
		dos.writeInt(t.size());
		while(e.hasMoreElements())
			dos.writeUTF(e.nextElement().getClass().toString());
		
	
	}
	
	/** This method adds a belief to the agent and is called by the
	 * affect manager when an actuator is adding a belief. This method differs
	 * from {@link #addFOSBelief(FOS) addFOSBelief(FOS)}, which is called by the
	 * perception manager. The belief is not added to
	 * the belief set during the current cycle. The reason for this is that commitment
	 * execution is nondeterministic. That is, the order in which commitments
	 * are fired in the current cycle is irrelevant at a logical level
	 *  and will not have a consequence on the resolution based reasoning
	 *  process for determining the desired states in the current cycle. Beliefs
	 *  added using this method will only be added to the agent's current belief
	 *  set after all of the commitments in the current cycle have been executed.
	 * @param fos the belief to be added.
	 */
	public void addActFOS(FOS fos) {

		if (actSize == actBelief.length) {
			FOS[] array = new FOS[actSize + ARRAY_SIZE];
			System.arraycopy(actBelief, 0, array, 0, actSize);
			actBelief = array;
		}
		actBelief[actSize] = fos;
		actSize++;
	}
	/** Adds a FOS to the agent's belief set. 
	 * 
	 * @param belief fos the FOS to be added to the agent's beliefs.
	 */
	public void addFOSBelief(FOS belief) {

		if (belief.functorEquals(ALWAYS)) {
			if (alwaysSize + 1 == alwaysBeliefs.length) {
				FOS[] newArray = new FOS[alwaysSize + ARRAY_SIZE];
				System.arraycopy(alwaysBeliefs, 0, newArray, 0, alwaysSize);
				alwaysBeliefs = newArray;
			}
			alwaysBeliefs[alwaysSize] = belief;
			alwaysSize++;

		} else if (belief.functorEquals(NEXT)) {
			if (nextSize + 1 == nextBeliefs.length) {
				FOS[] newArray = new FOS[nextSize + ARRAY_SIZE];
				System.arraycopy(nextBeliefs, 0, newArray, 0, nextSize);
				nextBeliefs = newArray;
			}
			nextBeliefs[nextSize] = belief;
			nextSize++;

		} else {

			// FOS keyFOS = belief.next();
			FOS keyFOS = belief;

			Node node = (Node) keyFOS.getFromTable(current);
			if (node == null)
				keyFOS.addToTable(current, new Node(belief));
			else {
				Node last = null;

				while (node != null) {
					if (node.fos.equals(belief))
						return;
					last = node;
					node = node.next;
				}
				node = new Node(belief);
				last.next = node;
			}
		}

	}
	/** This method causes an agent to drop a belief. It operates in 
	 * a similar manner to {@link #addActFOS(FOS) addActFOS(FOS)}.
	 * The belief is not retracted until the current control process
	 * has finished determining the desired states.  If the agent does not
	 * contain the specified belief, no error is thrown.
	 * 
	 * @param fos the belief that is to be retracted.
	 */
	public void retractBelief(FOS fos) {

		if (retSize == retract.length) {
			FOS[] array = new FOS[retSize + ARRAY_SIZE];
			System.arraycopy(retract, 0, array, 0, retSize);
			retract = array;
		}
		retract[retSize] = fos;
		retSize++;
	}
	/** This method updates the agent's current belief set. Initially all of the
	 * agent's perceptors are fired. The beliefs to be retracted are then removed
	 * from the set. Subsequently, the agent's temporal beliefs are
	 * added (Provided it is the correct cycle for next beliefs).
	 */
	public void updateBeliefs() {
		
		for (int i = actSize; i-- > 0;) {
			addFOSBelief(actBelief[i]);
			actBelief[i] = null;
		}
		actSize = 0;
		
		for (int i = perceptors.length; i-- > 0;)
			perceptors[i].perceive();

		for (int i = retSize; i-- > 0;) {

			FOS belief = retract[i];
			if (belief.functorEquals(ALWAYS))
				alwaysSize = removeBelief(belief, alwaysSize, alwaysBeliefs);

			else if (belief.functorEquals(NEXT))
				nextSize = removeBelief(belief, nextSize, nextBeliefs);
			else
				removeCurrent(belief);

			retract[i] = null;
		}
		retSize = 0;

		int tmp=nextSize;
		for (int i = nextSize; i-- > 0;) {
			
			addFOSBelief(nextBeliefs[i].next());
		}
		
		for(int i=tmp;i<nextSize;i++){
			nextBeliefs[i-tmp]=nextBeliefs[i];
		}
		int diff=nextSize-tmp;
		for(int i=0;i<tmp;i++){
			nextBeliefs[i+diff]=null;
		}
		
		nextSize-=tmp;
		
		for (int i = alwaysSize; i-- > 0;) {
			FOS fos = alwaysBeliefs[i].next();
			
			addFOSBelief(fos);
			alwaysBeliefs[i].reset();

		}

	}

	private void removeCommit(int i) {
		commitments[i].removeVarVals(this);
		int numMoved = commitSize - i - 1;
		
		// commitFire indexed 1 ahead
		if (numMoved > 0){
			
			System.arraycopy(commitments, i + 1, commitments, i, numMoved);
			
			System.arraycopy(commitFire,i+1,commitFire,i,numMoved);
		
		}
		
		commitFire[commitSize]=false;
		
		commitments[--commitSize] = null;
		
		
		
		
	}
	/** Performs a step in the agent's control algorithm. It should be noted
	 * that this method does not update the agent's beliefs. The updateBeliefs
	 * method must be called prior to this method for a complete cycle.
	 
	 */
	public void step() {
		try {
			
			roleLib.solve(this);

			solveRule(commitmentRules);

			
			buildKnapsack();

//			 Remember commitFire 1 index ahead
			for (int i = 0; i < commitSize;) {
				
				if (commitFire[i]) {
					
					Commitment nextCommitment = commitments[i];
					addActFOS(nextCommitment.toCommitBelief());
					
					if (nextCommitment.process(this, actuators)){
						i++;
						
					}else {
						
						removeCommit(i);
						
					}
				} else{
					
					removeCommit(i);
					
				}
				
				

			}
			commitFire=null;
			
			current.clear();
			
			for(int i=uvSize;i-->0;){
				
				Commitment c=(Commitment)tempMod.get(uv[i].id);
				if(c==null)System.err.println("No commitment for "+uv[i].id);
				else c.updateValues(uv[i].v,uv[i].c);
			}
			uvSize=0;

		} catch (Throwable th) {
			th.printStackTrace();

		}

	}

	

	private void buildKnapsack() {
		
		int n = commitSize + 1;
		int[] v = new int[n];
		int[] w = new int[n];
		int[]weighted=new int[n];
		
		int num=1;
		commitFire = new boolean[n];
		
		
		for (int i = 1; i < n; i++) {
			commitments[i - 1].addValues(v, w, i);
			if (w[i] == 0){
				commitFire[i-1] = true;
				
			}else{
				weighted[num++]=i;
				
			}
		}
		if(resources<0)return;
		int res = resources + 1;
		
		int[][] c = new int[num][res];
		boolean[][] b = new boolean[num][res];

		for (int k = 1; k < num; k++){
			for (int j = 1; j < res; j++) {
				
				int i=weighted[k];
				if (w[i] <= j && (v[i] + c[k - 1][j - w[i]]) > c[k - 1][j]) {
					
					c[k][j] = v[i] + c[k - 1][j - w[i]];
					b[k][j] = true;
					
					continue;
				}
				
				c[k][j] = c[k - 1][j];
				

			}
		}
		int sum = resources;
		
		// Remember commitFire 1 index ahead
		for (int i = num; i-- > 0 && sum > 0;) {

			if (b[i][sum]) {
				sum -= w[weighted[i]];
				
				commitFire[weighted[i]-1]=true;

			}
		}
		
		

	}

	/** Appends the agent's current and temporal beliefs to a string buffer.
	 * 
	 * @param bel the string buffer that the beliefs are appended to.
	 */
	public void belBuf(StringBuffer bel) {
		Enumeration enumer = current.elements();

		while (enumer.hasMoreElements()) {
			Node node = (Node) enumer.nextElement();

			while (node != null) {
				node.fos.append(bel);
				bel.append("\n");
				node = node.next;
			}
		}

		for (int i = 0; nextBeliefs[i] != null; i++) {
			nextBeliefs[i].append(bel);
			bel.append("\n");
		}

		for (int i = 0; alwaysBeliefs[i] != null; i++) {
			alwaysBeliefs[i].append(bel);
			bel.append("\n");
		}

	}
	/** Appends a string representation of each of the agent's current
	 * commitments to a string buffer.
	 * @param commit the string buffer that the string representation of the
	 * commitments are appended to.
	 */
	public void commitBuf(StringBuffer commit) {

		for (int i = 0; commitments[i] != null; i++) {
			commitments[i].append(commit);
			commit.append("\n");
		}

	}

	private void removeCurrent(FOS belief) {
		FOS keyFOS = belief.next();

		Node node = (Node) keyFOS.getFromTable(current);
		Node last = null;
		while (node != null) {
			if (node.fos.equals(belief)) {
				if (last == null) {
					keyFOS.removeFromTable(current);
					if (node.next != null)
						keyFOS.addToTable(current, node.next);
					break;
				}
				last.next = node.next;
				break;
			}
			last = node;
			node = node.next;
		}
	}

	private int removeBelief(FOS bel, int size, FOS[] belief) {

		for (int j = size; j-- > 0;) {
			if (bel.equals(belief[j])) {
				int numMoved = size - j - 1;
				if (numMoved > 0)
					System.arraycopy(belief, j + 1, belief, j, numMoved);
				belief[--size] = null;
			}
		}
		return size;

	}
	/** Solves a list of commitment rules and subsequently clears the solutions.
	 * If a commitment rule is evaluated to true for at least one binding,
	 * a commitment will be added to the desired states or commitment set.
	 * 
	 * @param rule the list of rules to be evaluated.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void solveRule(TerImplication[] rule) throws MalformedLogicException {

		for (int i = rule.length; i-- > 0;)
			rule[i].solve(this);

		for (int i = rule.length; i-- > 0;)
			rule[i].seqClear();

		/*
		 * System.out.println(); for(int i=rule.length;i-->0;){ //StringBuffer
		 * sb=new StringBuffer(); //rule[i].append(sb); rule[i].printState();
		 * //System.out.println(sb); java.util.Enumeration e=current.elements();
		 * while(e.hasMoreElements()){
		 * System.out.println(((Node)e.nextElement()).fos); } }
		 */

	}
	/** Adds a commitment to the agent's current commitment set.
	 * 
	 * @param commit the commitment that is to be added.
	 */
	public void adoptCommitment(Commitment commit) {
		
		if (commitSize + 1 == commitments.length) {
			Commitment[] newArray = new Commitment[commitments.length
					+ ARRAY_SIZE];
			System.arraycopy(commitments, 0, newArray, 0, commitments.length);
			commitments = newArray;
		}
		commitments[commitSize] = commit;
		commitSize++;
		commit.variableVals(this);
	
		
	}
	
	/** Adds a child commitment to the agent's current commitment set.
	 * 
	 * @param commit the child commitment to be added.
	 */
	public void adoptChild(Commitment commit){
		// assuming children are atomic actions and have
		// weights of 0 and values of 1
		
		if(commitSize+ 1==commitFire.length){
			boolean[]temp=new boolean[commitFire.length<<1];
			System.arraycopy(commitFire,0,temp,0,commitFire.length);
			commitFire=temp;
		}
		
		
		commitFire[commitSize]=true;
		adoptCommitment(commit);
		
		
		
	}
	/** For each trigger of a role template, checks if the trigger
	 * matches a current belief. If the trigger does match, a substitution
	 * set is created for the variables of the trigger. The substitution set
	 * is subsequently applied to the role template to create a role instance.
	 * 
	 * @param trigger an array of triggers for the role template.
	 * @param table the hash table that the role instance is to be added to.
	 * @param template the specified role template.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void handleTrigger(FOS[] trigger, Hashtable table,
			RoleTemplate template) throws MalformedLogicException {
		for (int i = trigger.length; i-- > 0;) {
			FOS trig = trigger[i];
			// trig.reset();
			// FOS keyFOS = trig.next();
			// trig.reset();

			Node node = (Node) trig.getFromTable(current);

			while (node != null) {
				FOS belief = node.fos;
				if (trig.matches(belief)) {
					SubstitutionSet set = new SubstitutionSet(null);
					trig.buildSet(belief, set);
					// System.out.println("handling "+belief);
					template.createInstance(set, table);
				}
				node = node.next;
			}
		}
	}
	/** Adds variables to the substitution set for the specified
	 * FOS if there are current beliefs that
	 * match with the FOS and provided the substitution
	 * set is not null. If the substitution set is null, returns true
	 * if the FOS matches a current belief,
	 * false otherwise.
	 * 
	 * @param fos the specified FOS.
	 * @param set the substitution set to add variable mappings to.
	 * @return true if the FOS matches a current belief
	 * and the substitution set is null, false otherwise.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public boolean processFOS(FOS fos, SubstitutionSet set)
			throws MalformedLogicException {

		Node node = (Node) fos.getFromTable(current);

		while (node != null) {
			FOS belief = node.fos;

			if (fos.matches(belief)) {
				if (set == null)
					return true;
				set.createSolution(this, fos, belief);
			}
			node = node.next;
		}

		return false;
	}

}
