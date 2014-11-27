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
package com.agentfactory.cldc.compiler;

import static com.agentfactory.cldc.compiler.Container.BL_V;
import static com.agentfactory.cldc.compiler.Container.DEP_E;
import static com.agentfactory.cldc.compiler.Container.DEP_L;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

/** This class stores information related to commitment rules.
 * 
 * @author Conor Muldoon
 *
 */
public class Rule {
	// Ideally left and right should be ArrayLists
	String left, right, name;

	ArrayList<String> label;

	ArrayList<Boolean> bool;

	/** Creates an instance of Rule.
	 * 
	 * @param s1 the belief sentence.
	 * @param s2 the commitment.
	 * @param al the list of labels the rule is dependent on.
	 * @param n the label for the rule.
	 */
	public Rule(String s1, String s2, ArrayList<String> al, String n) {
		bool = new ArrayList<Boolean>();
		left = s1;
		right = s2;
		label = al;
		int num = label.size();
		for (int i = num; i-- > 0;)
			bool.add(true);
		for (int i = 0; i < num; i++) {
			String s = label.get(i);

			if (s.charAt(0) == '!') {

				bool.set(i, false);
				label.set(i, s.substring(1));
			}
		}
		name = n;

	}

	/** Checks for cycles in the rule label map and adds rules in sequential order to the sorted list.
	 * 
	 * @param al the list of previous labels.
	 * @param map a mapping between labels and rules.
	 * @param sorted the sorted list.
	 * @throws CycleException if there is a cyclical dependency between labels.
	 * @throws LabelException if there is an unknown label.
	 */
	public void checkCycles(ArrayList<String> al, Map<String, Rule> map,
			ArrayList<String> sorted) throws CycleException, LabelException {
		if(left.equals("")){
			for(String s:label){
				Rule r=map.get(s);
				r.checkCycles(al,map,sorted);
			}
		}else if (al.contains(name)) {

			throw new CycleException(
					"Error Cycle Dependency for belief label: " + name);
		} else {
			al.add(name);
			
			removeBlank(map);
			
			for (String s : label) {
				Rule r = map.get(s);
				
				
				if (r == null)
					throw new LabelException("Unknown Label: " + s);
				
				else r.checkCycles(al, map,sorted);
			}
			
			if(!sorted.contains(name))
				sorted.add(name);
		}

	}
	
	
	/** Add the rule to a map. The name is used as the key.
	 * 
	 * @param map the map the rule is added to.
	 */
	public void addTo(Map<String, Rule> map) {

		map.put(name, this);
	}

	/** Check whether the rule has a dependency on the specified rule.
	 * 
	 * @param r the specified rule.
	 * @return true if there is a dependency, false otherwise.
	 */
	public boolean depends(Rule r) {
		int n = r.label.size();
		for (int i = 0; i < n; i++)
			if (name.equals(r.label.get(i)))
				return true;
		return false;
	}

	/** Prints the name of the rule to the print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void printName(PrintStream ps) {
		ps.print(name);
	}

	/** Prints the belief sentence to the print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void printLeft(PrintStream ps) {
		ps.print(left);
	}

	/** Prints the commitment to the print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void printRight(PrintStream ps) {
		ps.print(right);
	}

	/**
	 * Prints the number of label dependencies the rule has.
	 * 
	 * @param ps the specified print stream.
	 */
	public void printNum(PrintStream ps) {
		ps.print(label.size());
	}

	/** Tests whether the rule has a label.
	 * 
	 * @return true if the rule name equals "", false otherwise.
	 */
	public boolean notSeq() {
		return name.equals("");
	}

	
	private void removeBlank(Map<String,Rule>map){
		for(int i=0;i<label.size();i++){
			String s=label.get(i);
			Rule r = map.get(s);
			if(r.left.equals("")){
				
				label.remove(i);
				
				bool.remove(i);
				
				for(String s1:r.label){
					label.add(s1);
					if (s1.charAt(0) == '!') {
						bool.add(false);
					}else bool.add(true);
				}
			}
		}
	}
	/** Prints labels to the print stream.
	 * 
	 * @param output the output tokens.
	 * @param j the current index.
	 * @param n the maximum index.
	 * @param ps the print stream.
	 * @param map a mapping between labels and rules.
	 * @return the end index.
	 */
	public int loopReq(java.util.ArrayList<Object> output, int j, int n,
			PrintStream ps,Map<String,Rule>map) {
		
		removeBlank(map);
		
		int y = j + 1;
		for (; y < n; y++)
			if (output.get(y) == DEP_E)
				break;

		// if(!labName.equals(""))return y;
		// System.out.println("in loop req");
		int nl = label.size();
		for (int k = 0; k < nl; k++) {
			for (int l = j + 1; l < y; l++) {
				Object o = output.get(l);
				if (o == DEP_L)
					ps.print(label.get(k));
				else if (o == BL_V)
					ps.print(bool.get(k));
				else
					Container.code(ps, o, k);
			}
		}
		// System.out.println("finished loop req");
		return y;
	}
}
