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

import static com.agentfactory.cldc.compiler.Container.IDT;
import static com.agentfactory.cldc.compiler.Container.LOOP;
import static com.agentfactory.cldc.compiler.Container.NTR;
import static com.agentfactory.cldc.compiler.Container.NUMR;
import static com.agentfactory.cldc.compiler.Container.RULE_S;
import static com.agentfactory.cldc.compiler.Container.SEQ_S;
import static com.agentfactory.cldc.compiler.Container.TE;
import static com.agentfactory.cldc.compiler.Container.TR;
import static com.agentfactory.cldc.compiler.Container.TS;
import static com.agentfactory.cldc.compiler.Container.loopCode;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

/** This class stores information related to agent roles.
 * 
 * @author Conor Muldoon
 *
 */
public class Role {

	// ArrayList<Rule>rule;
	ArrayList<Rule> terImp;

	Map<String, Rule> labels;

	ArrayList<String> trig;
	ArrayList<String>sorted;

	String id;

	int numR;
	
	// to do: handle embedded labels
	

	/** Creates an instance of Role.
	 * @param ident an identifier for the role.
	 * @param r the commitment rules.
	 * @param t the triggers.
	 * 
	 * @throws CycleExcpetion if there is a cyclical dependency between labels.
	 * @throws LabelException if there is an unknown label.
	 */
	public Role(String ident, ArrayList<Rule> r, ArrayList<String> t) throws CycleException,LabelException{
		id = ident;
		terImp = new ArrayList<Rule>();
		sorted=new ArrayList<String>();
		labels = new java.util.Hashtable<String, Rule>();
		trig = t;
		for (Rule rl : r)
			if (rl.notSeq())
				terImp.add(rl);
			else
				rl.addTo(labels);
		numR=terImp.size();
		Design.sortSeq(labels,sorted);
	}

	/**  Applies the role to print stream subject to the specified tokens.
	 * 
	 * @param items a list of tokens.
	 * @param ps the specified print stream.
	 */
	public void process(ArrayList<Object> items, PrintStream ps) {
		int n = items.size();

		for (int i = 0; i < n; i++) {
			Object o = items.get(i);
			if (o == LOOP)
				ps.print(i);
			else if (o == NUMR)
				ps.print(numR);
			else if (o == IDT)
				ps.print(id);
			else if (o == NTR)
				ps.print(trig.size());
			// else if(o==RS)i=loopCode(rule,items,i,ps,RE,RL);
			else if (o == TS)
				i = loopCode(trig, items, i, ps, TE, TR);
			else if (o == RULE_S)
				i = Design.ruleCode(labels, terImp, items, i, ps);
			else if (o == SEQ_S) {
				
				i = Design.seqCode(labels, sorted, items, i, ps);

			} else
				ps.print(o);
			// to do: handle embedded labels

		}

	}
}
