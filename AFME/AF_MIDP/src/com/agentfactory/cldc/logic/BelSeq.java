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

/* A belief sequence class */

/** Belief sequences (or more aptly belief lists) are used in AFME to identify the conditions
 * under which agents should adopt commitments. In AFME, support is provided
 * for belief labeling. With belief labeling, the developer can encode
 * dependencies a belief list has on other belief lists. 
 * The BelSeq class represents a list of beliefs and associated dependencies
 * on other belief lists. It contains functionality for determining if a particular
 * belief sentence evaluates to true. If a belief sentence evaluates to true
 * for one or more binding, a commitment will added to the agent.
 * At a logical level, the use of belief
 * sentences to encode agent behaviour is somewhat similar to the use of resolution-based
 * theorem proving in Prolog.
 * 
 * 
 * 
 * @author Conor Muldoon
 * 
 */
public class BelSeq {

	FOS[] arr;

	BelSeq[] belArr;

	boolean[] boolArr;

	SubstitutionSet[] sol;

	SubstitutionSet[] lab;

	// SubstitutionSet[]val;
	int size;

	int val = 0;

	int belSize = 0;

	int pos;

	// []sol required because compiler or control engine do not
	// guarantee order of evaluation of belief labels. A belief sequence
	// may be evaluated before other belief sequences that it depends on. Thus
	// the result must be stored. This is what []sol is used for.

	final static char DELIM = ',';

	final static char SEP = '|';

	// int n;
	// int j;

	final static byte SIZE = 4;

	SubstitutionSet solution;

	boolean tested = false;

	// boolean solved=false;

	private void init() {
		lab = new SubstitutionSet[SIZE];
		sol = new SubstitutionSet[SIZE];
		solution = new SubstitutionSet(this);

		// j = -1;
	}

	/** Creates a new instance of BelSeq.
	 * 
	 * @param string a string representation of the belief list.
	 * @param bArr the BelSeqs that this BelSeq has dependencies on.
	 * @param bool this array is used to determine whether the elements
	 * of bArr are negated dependencies. The arrays are associated through
	 * their index number.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public BelSeq(String string, BelSeq[] bArr, boolean[] bool)
			throws MalformedLogicException {

		init();
		belArr = bArr;
		// belSize = bSize;
		boolArr = bool;
		if (!string.equals("")) {

			int maxPosition = string.indexOf(SEP);

			int tcount = Integer.parseInt(string.substring(maxPosition + 1));
			int currpos = 0;
			int b = 0;
			arr = new FOS[tcount];
			if (tcount == 1) {
				arr[0] = FOS.createFOS(string.substring(0, maxPosition));
			} else {
				int cnt = 0;
				int prev = 0;

				// decrement tcount for last check in while
				tcount--;
				for (; currpos < maxPosition; currpos++) {
					char c = string.charAt(currpos);
					if (c == FOS.OPEN_BRACKET)
						b++;
					else if (c == FOS.CLOSE_BRACKET)
						b--;

					if (b == 0 && (c == DELIM)) {
						arr[cnt] = FOS.createFOS(string
								.substring(prev, currpos));
						prev = currpos + 1;
						cnt++;
						if (cnt == tcount)
							break;
					}

				}
				arr[cnt] = FOS.createFOS(string.substring(prev, maxPosition));

			}
		}

	}

	private BelSeq(FOS[] fArr, BelSeq[] bArr, boolean[] bl, int p) {
		init();
		arr = fArr;
		belArr = bArr;
		// belSize = bSize;
		boolArr = bl;
		pos = p;

	}

	/** Creates commitment objects and adds them to the specified
	 * agents by applying solutions of the belief sentence to the commitment first
	 * order structure.
	 * 
	 * @param right the commitment FOS.
	 * @param agent the agent adopts the commitments.
	 */
	public void createCommit(FOS right, Agent agent) {

		for (int i = size; i-- > 0;) {
			//FOS f=FOS.createFOS(right.toString()).apply(sol[i]);
			//String s=f.toString();
			//System.out.println(right.toString());
			//System.out.println(s);
			
			createCommit(agent,right.apply(sol[i]));
			//sol[i].display();
			//System.out.println();
			sol[i].clear();
		}

		// }

	}

	/** Crates a commitment from the specified FOS and adds it the specified
	 * agent's commitment set.
	 * 
	 * @param agent the agent to adopt the commitment.
	 * @param fos a FOS representation of the commitment.
	 */
    public static void createCommit(Agent agent,FOS fos){
    	
    	fos.reset();
		FOS first = fos.next();
		StringBuffer buffer = new StringBuffer();
		
		fos.next().append(buffer);
		Calendar time = Calendar.getInstance();
		if (buffer.charAt(0) == '+') {
			buffer.deleteCharAt(0);
			int total_mins = Integer.parseInt(buffer.toString());
			
			time.set(Calendar.SECOND, time.get(Calendar.SECOND)
						+ total_mins);
		}
		agent.adoptCommitment(new Commitment(first, time, fos.next(), fos
				.next(), null,fos.next(),fos.next(),fos.next()));
    }
    
	// }
	// }

	/**
	 * Clears the solved state of the belief sentence and wipes all solutions.
	 */
	public void clear() {

		for (int i = size; i-- > 0;) {
			sol[i] = null;
		}

		for (int i = belSize; i-- > 0;)
			lab[i] = null;

		belSize = 0;

		size = 0;
		solution.wipe();
		tested = false;

	}

	private void addItem(SubstitutionSet ss) {

		if (belSize == lab.length) {
			SubstitutionSet[] temp = new SubstitutionSet[size << 1];
			System.arraycopy(lab, 0, temp, 0, size);
			lab = temp;
		}

		lab[belSize++] = ss.copy();

	}

	private void solveItem(SubstitutionSet ss) {
		val++;
		if (val == belArr.length)
			addItem(ss);
		else if (boolArr[val])
			for (int i = 0; i < belArr[val].size; i++) {
				if (ss.construct(belArr[val].sol[i]))
					solveItem(ss);
				ss.clear();
			}
		else {
			solveItem(ss);

		}
		val--;

	}

	/**
	 * Clears each of the BelSeqs that this BelSeq has a dependency
	 * on provided the BelSeq has been tested or solved.
	 *
	 */
	public void seqClear() {
		for (int i = belArr.length; i-- > 0;)
			if (belArr[i].tested)
				belArr[i].clear();
	}

	/** Recursively solves the BelSeqs that this BelSeq has
	 * a dependency on. If the dependencies do no hold, returns
	 * false. If the dependencies do hold, solves this BelSeq.
	 * If one or more solution is found, returns true. 
	 * 
	 * @param agent the agent the BelSeq belongs to.
	 * @return true if there is at least one solution, false otherwise.
	 */
	public boolean solveSeq(Agent agent) {
		tested = true;
		int n = belArr.length;
		if (n > 0) {
			int t = 0;
			boolean first = false;
			for (int i = 0; i < n; i++) {
				if (belArr[i].tested == false)
					belArr[i].solveSeq(agent);

				if (!boolArr[i]) {
					if (belArr[i].size > 0)
						return false;
				} else if (first == false) {
					first = true;
					t = i;
				}

			}

			val = t;
			for (int i = t; i < belArr[t].size; i++) {
				solveItem(belArr[t].sol[i]);
			}
			if (first && belSize == 0)
				return false;

		}
		solve(agent);
		if (size == 0)
			return false;
		return true;
	}
	
	private void addSolution() {
		if (size == sol.length) {
			SubstitutionSet[] temp = new SubstitutionSet[size << 1];
			System.arraycopy(sol, 0, temp, 0, size);
			sol = temp;
		}

		sol[size++] = solution.copy();
		
	}

	private void createBelSol(){
		for (int i = 0; i < belSize; i++) {
			if (solution.construct(lab[i]))
				addSolution();
			solution.clear();
		}
		solution.wipe();
	}
	
	private void solve(Agent agent) throws MalformedLogicException {
		
		if(arr==null){
			createBelSol();
			
			return;
		}
		
		if (pos == arr.length) {
			// System.out.println("have solution "+size);
			if (belSize == 0) {
				addSolution();
				solution.clear();
			} else
				createBelSol();

			// System.out.println("have solution");
			return;
		}
		
		if (arr[pos].isNot()) {
			try {
				FOS goal = arr[pos].invert().apply(solution);
				if (goal.isTrue() || agent.processFOS(goal, null))
					return;
				recurSolve(agent);
			} catch (MalformedLogicException e) {
				e.printStackTrace();
			}
			return;
		}
		if (arr[pos].isTrue()) {
			recurSolve(agent);
			return;
		}
		if(arr[pos].isExpression()){
			if(arr[pos].apply(solution).eval()){
				recurSolve(agent);
			}
			return;
		}
		if (pos > 0)agent.processFOS(arr[pos].apply(solution),solution);
		else agent.processFOS(arr[pos], solution);

	}

	/** Recursively solves the belief sequence. If the belief
	 * sequence is evaluated to true for one or more bindings,
	 * a solution is added to the list of solutions.
	 * 
	 * @param agent the agent this belief sequence belongs to.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void recurSolve(Agent agent) throws MalformedLogicException {
		pos++;
		solve(agent);
		pos--;
	}

	/** Recursively applies the substitution set to the attributes of this
	 * BelSeq to create an applied belief sequence. When a substitution set is
	 * applied to something, it replaces variables with associated instances.
	 * 
	 * @param ss the substitution set applied to the beliefs and belief list dependencies of this BelSeq.
	 * @return the applied BelSeq.
	 */
	public BelSeq createAppliedSeq(SubstitutionSet ss) {
		int len = arr.length;
		FOS[] l = new FOS[len];
		for (int i = len; i-- > 0;)
			l[i] = arr[i].apply(ss);
		BelSeq[] bArr = new BelSeq[belArr.length];
		for (int i = belArr.length; i-- > 0;) {
			bArr[i] = belArr[i].createAppliedSeq(ss);
		}
		return new BelSeq(l, bArr, boolArr, pos);

	}

	private void appendDelim(StringBuffer buffer) {
		buffer.append(' ');
		buffer.append(DELIM);
		buffer.append(' ');
	}

	/** Appends the belief sequence to a string buffer.
	 * 
	 * @param buffer the string buffer that the belief sequence 
	 * is to be appended to.
	 */
	public void append(StringBuffer buffer) {
		// arr[0].printState();
		if (arr != null) {
			int n = arr.length;
			arr[0].append(buffer);

			for (int i = 1; i < n; i++) {
				appendDelim(buffer);
				arr[i].append(buffer);
			}
		}
		for (int i = 0; i < belArr.length; i++) {
			appendDelim(buffer);
			belArr[i].append(buffer);
		}
	}

}
