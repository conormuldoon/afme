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
/** A substitution set provides a mapping from AFME variables to FOS
 * instances. 
 * 
 * @author Conor Muldoon
 */
public class SubstitutionSet {

	FOS[] first, second;

	int size;
	int n;
	final static byte SIZE = 8;

	BelSeq seq;

	/** Constructs a substitution set for a belief sequence.
	 * 
	 * @param bSeq the specified belief sequence.
	 */
	public SubstitutionSet(BelSeq bSeq) {
		first = new FOS[SIZE];
		second = new FOS[SIZE];
		seq=bSeq;
	}

	private SubstitutionSet(FOS[] f1, FOS[] f2, BelSeq bSeq,int s) {
		first = f1;
		second = f2;
		seq=bSeq;
		size=s;
	}

	/** Creates a copy of the substitution set.
	 * 
	 * @return the cloned substitution set.
	 */
	public SubstitutionSet copy() {
		int num = size << 1;
		return new SubstitutionSet(createArray(first, size, num), createArray(
				second, size, num), seq,size);

	}
	/** Adds the contents of the specified substitution set to this substitution set.
	 * 
	 * @param ss the specified substitution set.
	 * @return false if the specified substitution set and this substitution set
	 * contain different mappings.
	 */
	public boolean construct(SubstitutionSet ss){
		n=size;
		for(int i=0;i<size;i++){
			for(int j=ss.size;j-->0;){
				if(first[i]==ss.first[j]){
					if(!second[i].equals(ss.second[j])){
						//clear();
						return false;
					}
					continue;
				}
				add(ss.first[i],ss.second[i]);
			}
		}
		//seq.recurSol(this);
		return true;
		
	}
	
	/** Recursively creates solutions using the agent object for the
	 * specified FOSs.
	 * 
	 * @param agent the agent used in the creation of a solution.
	 * @param f1 a FOS condition.
	 * @param f2 a current belief matching the condition.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void createSolution(Agent agent, FOS f1, FOS f2)
			throws MalformedLogicException {
		n = size;
		f1.buildSet(f2, this);
		
		seq.recurSolve(agent);
		
		clear();
		

	}
	
	/** Removes all currently stored mappings.
	 * 
	 *
	 */
	public void wipe(){
		n=0;
		clear();
	}
	/** Clears state back to the previous execution of the {@link #construct(SubstitutionSet) construct(SubstitutionSet)} method,
	 * or all elements if called from {@link #wipe() wipe()}.
	 * 
	 *
	 */
	public void clear(){
		for (int i = size; i-- > n;) {
			first[i] = null;
			second[i] = null;
		}
		size = n;
	}

	/** Returns an instance for the specified variable.
	 * 
	 * @param var the variable to be replaced.
	 * @return the instance that matches the variable.
	 */
	public FOS replaceVar(FOS var) {
		for (int i = size; i-- > 0;)
			if (first[i] == var)
				return second[i];
		return var;
	}

	/** Adds a mapping between a variable and a FOS instance.
	 * 
	 * @param f1 the variable.
	 * 
	 * @param f2 the FOS instance.
	 */
	public void add(FOS f1, FOS f2) {

		if (size == first.length) {
			int num = size << 1;
			first = createArray(first, size, num);
			second = createArray(second, size, num);
		}
		first[size] = f1;
		second[size] = f2;
		size++;
	}

	private FOS[] createArray(FOS[] fos, int size, int num) {
		FOS[] newArray = new FOS[num];
		System.arraycopy(fos, 0, newArray, 0, size);
		return newArray;
	}
	
	

}
