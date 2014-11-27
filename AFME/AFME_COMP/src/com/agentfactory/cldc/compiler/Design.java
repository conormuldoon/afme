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

import static com.agentfactory.cldc.compiler.Container.AE;
import static com.agentfactory.cldc.compiler.Container.AS;
import static com.agentfactory.cldc.compiler.Container.B_NUM;
import static com.agentfactory.cldc.compiler.Container.CLS;
import static com.agentfactory.cldc.compiler.Container.COMM_E;
import static com.agentfactory.cldc.compiler.Container.COMM_S;
import static com.agentfactory.cldc.compiler.Container.DELM;
import static com.agentfactory.cldc.compiler.Container.DEP_L;
import static com.agentfactory.cldc.compiler.Container.DEP_S;
import static com.agentfactory.cldc.compiler.Container.DN;
import static com.agentfactory.cldc.compiler.Container.IE;
import static com.agentfactory.cldc.compiler.Container.INI_C;
import static com.agentfactory.cldc.compiler.Container.IS;
import static com.agentfactory.cldc.compiler.Container.ME;
import static com.agentfactory.cldc.compiler.Container.MS;
import static com.agentfactory.cldc.compiler.Container.NMA;
import static com.agentfactory.cldc.compiler.Container.NMP;
import static com.agentfactory.cldc.compiler.Container.NUMR;
import static com.agentfactory.cldc.compiler.Container.PE;
import static com.agentfactory.cldc.compiler.Container.PS;
import static com.agentfactory.cldc.compiler.Container.RLE;
import static com.agentfactory.cldc.compiler.Container.RLS;
import static com.agentfactory.cldc.compiler.Container.RULEE;
import static com.agentfactory.cldc.compiler.Container.RULE_S;
import static com.agentfactory.cldc.compiler.Container.R_L;
import static com.agentfactory.cldc.compiler.Container.R_R;
import static com.agentfactory.cldc.compiler.Container.SEQ_E;
import static com.agentfactory.cldc.compiler.Container.SEQ_S;
import static com.agentfactory.cldc.compiler.Container.code;
import static com.agentfactory.cldc.compiler.Container.constructDesign;
import static com.agentfactory.cldc.compiler.Container.loopCode;

import java.io.PrintStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** This class stores information related to agent designs. It is used
 * in the generation of output files.
 * 
 * @author Conor Muldoon
 *
 */
public class Design {

	// ArrayList<Rule>rule;
	ArrayList<Rule> terImp;

	Map<String, Rule> labels;

	ArrayList<String> act;

	ArrayList<String> per;

	ArrayList<String> mod;

	ArrayList<String> sorted;
	ArrayList<String> initialCommitments;

	String iMod;

	Collection<Role> role;

	URLClassLoader loader;
	
	private final static char DELIM=',';
	
	// int numR;
	
	/**Creates and instance of Design.
	 * @param l the specified class loader.
	 */
	public Design(URLClassLoader l) {
		
		loader = l;
		
		// rule=new ArrayList<Rule>();
		terImp = new ArrayList<Rule>();
		sorted = new ArrayList<String>();
		labels = new java.util.Hashtable<String, Rule>();

		act = new ArrayList<String>();
		per = new ArrayList<String>();
		mod = new ArrayList<String>();
		role = new ArrayList<Role>();
		initialCommitments=new ArrayList<String>();

	}

	/** Orders the sequence of labels.
	 * 
	 * @throws LabelException if there is an unknown label.
	 * @throws CycleException if there is a cyclical dependency between labels.
	 */
	public void sortSeq() throws LabelException, CycleException {
		sortSeq(labels, sorted);
	}

	/** Constructs a design for the file.
	 * 
	 * @param file the specified file.
	 */
	public void imp(String file) {
		constructDesign(this, file, loader);
	}

	/** Adds an initial commitment.
	 * 
	 * @param arguments the commitment arguments.
	 * @param constraints the commitment constraints.
	 */
	public void addInitialCommitment(String arguments,String constraints){
		int ind=arguments.length()-1;
		int num=arguments.charAt(ind)-'0';
		if(num>4){
			System.out.println("Problem with initial commitment: "+arguments.substring(0,ind-1)+constraints);
			System.out.println("Too many arguments specified.");
		}else{
			String v,c;
			String id=null;
			if(constraints.equals("")){
				v="1";
				c="0";
			}
			else{
				String[]str=constraints.split(",");
				v=str[0];
				c=str[1];
				if(str.length>2){
					id=str[2];
				}
			}
			String s[] = new String[4];
			int k=0;
			
			ind--;
			for(int j=0;j<num;j++){
				StringBuffer sb=new StringBuffer();
				int b=0;
				
				loop:for(;k<ind;k++){
					
					char ch=arguments.charAt(k);
					if(ch=='(')b++;
					else if(ch==')')b--;
					if(ch==DELIM&&b==0){
						k++;
						break loop;
					}
					sb.append(ch);
					
				}
				
				s[(num-1)-j]=sb.toString();
				
				
			}
			
			
			StringBuffer sb=new StringBuffer("commit(");
			
			if(num<4)s[3]="?self";

			if(num<3)s[2]="?now";
				
			if(num==1)s[1]="true";
				
			sb.append(s[3]);
			sb.append(DELIM);
			sb.append(s[2]);
			sb.append(DELIM);
			sb.append(s[1]);
			sb.append(DELIM);
			sb.append(s[0]);
			sb.append(DELIM);
			sb.append(v);
			sb.append(DELIM);
			sb.append(c);
			if(id!=null){
				sb.append(DELIM);
				sb.append(id);
			}
			sb.append(')');
			initialCommitments.add(sb.toString());
		}
		
	}
	
	/** Adds a role.
	 * 
	 * @param s the role identifier.
	 * @param rule the role commitment rules.
	 * @param trig the role triggers.
	 */
	public void addRole(String s, ArrayList<Rule> rule, ArrayList<String> trig) {
		try {
			role.add(new Role(s, rule, trig));
		} catch (CycleException e) {
			e.printStackTrace();
		} catch (LabelException e) {
			e.printStackTrace();
		}

	}

	/** Adds a rule.
	 * 
	 * @param r the specified rule.
	 */
	public void addRule(Rule r) {
		
		if (r.notSeq())
			terImp.add(r);
		else {
			r.addTo(labels);
		}
	}

	/** Adds a word.
	 * 
	 * @param s the specified word.
	 */
	public void addWord(String s) {
		System.err.println("Warning WORD not defined for this compiler");
		System.err.println("Input string: " + s);
	}

	/** Adds an escape sequence text block.
	 * 
	 * @param s the specified text block.
	 */
	public void addEs(String s) {
		System.err.println("Warning ESC not defined for this compiler");
		System.err.println("Input string: " + s);
	}

	/** Adds an actuator.
	 * 
	 * @param s the actuator class.
	 */
	public void addAct(String s) {
		act.add(s);

	}

	/** Adds a perceptor.
	 * 
	 * @param s the perceptor class.
	 */
	public void addPer(String s) {
		per.add(s);
	}

	/** Sorts belief labels represented in a map and adds them sequentially to a list.
	 * 
	 * @param labels the map representation of belief labels.
	 * @param sorted the list the sort labels are added to.
	 * @throws LabelException if labels there is an unknown label.
	 * @throws CycleException if there are cyclical dependencies between cycles.
	 */
	public static void sortSeq(Map<String, Rule> labels,
			ArrayList<String> sorted) throws LabelException, CycleException {

		// Check for cycles and sort and prune
		Set<String> set = labels.keySet();
		for (String s : set) {
			Rule r = labels.get(s);
			ArrayList<String> al = new ArrayList<String>();
			r.checkCycles(al, labels, sorted);
		}

	}

	/** Applies the design to the output and prints to a stream.
	 * 
	 * @param ps the specified print stream.
	 * @param output the output tokens.
	 * @param fn the file name.
	 */
	public void process(PrintStream ps, ArrayList<Object> output, String fn) {
		//		

		int n = output.size();
		for (int i = 0; i < n; i++) {
			Object o = output.get(i);
			if (o == DN) {
				ps.print(fn.replaceAll("\\.", DELM));
			} else if (o == NUMR) {
				ps.print(terImp.size());
			} else if (o == NMA) {
				ps.print(act.size());
			}else if (o == NMP) {
				ps.print(per.size());
			} else if (o == PS) {
				i = loopCode(per, output, i, ps, PE, CLS);
			} else if (o == AS) {
				i = loopCode(act, output, i, ps, AE, CLS);
				// }else if(o==RS){
				// i=loopCode(rule,output,i,ps,RE,RL);
			} else if (o == MS)
				i = loopCode(mod, output, i, ps, ME, CLS);
			else if (o == RULE_S) {

				i = ruleCode(labels, terImp, output, i, ps);
			} else if(o ==COMM_S){
				i=loopCode(initialCommitments,output,i,ps,COMM_E,INI_C);
			}else if (o == IS) {
				
				int j = i+1;
				for (; j < n; j++) {
					o = output.get(j);
					if (o == IE){
						
						break;
						
					}
					if (iMod == null)
						continue;
					if (o == CLS)
						ps.print(iMod);
					else{
						
						ps.print(o);
					}
				}
				i = j;
			} else if (o == RLS) {
				ArrayList<Object> al = new ArrayList<Object>();
				int j = 0;
				for (j = i + 1; j < n; j++) {
					o = output.get(j);
					if (o == RLE)
						break;
					
					al.add(o);
				}
				for (Role r : role) {
					r.process(al, ps);
				}
				i = j;

			} else if (o == SEQ_S) {

				i = seqCode(labels, sorted, output, i, ps);

			} else
				code(ps, o, i);

		}

	}

	/** Prints labelled rules to a stream.
	 * 
	 * @param rule a mapping of labels to rules.
	 * @param sorted a sorted list of labels.
	 * @param output the output tokens.
	 * @param i the current index.
	 * @param ps the output print stream.
	 * @return the end index.
	 */
	public static int seqCode(Map<String, Rule> rule, ArrayList<String> sorted,
			ArrayList<Object> output, int i, PrintStream ps) {
		int x = i;
		int n = output.size();
		for (; x < n; x++)
			if (output.get(x) == SEQ_E)
				break;

		int num = 0;
		for (String s : sorted) {
			Rule r = rule.get(s);

			for (int j = i + 1; j < x; j++) {
				Object o = output.get(j);
				if (o == R_L)
					r.printLeft(ps);
				else if (o == DEP_L)
					r.printName(ps);
				else if (o == B_NUM)
					r.printNum(ps);
				else if (o == DEP_S) {
					j = r.loopReq(output, j, n, ps, rule);
				} else
					code(ps, o, num);

			}
			num++;
		}

		return x;
	}

	/** Prints rules to the output stream.
	 * 
	 * @param map a mapping of labels to rules.
	 * @param rule a list of rules.
	 * @param output the output tokens.
	 * @param i the current index.
	 * @param ps the output print stream.
	 * @return the end index.
	 */
	public static int ruleCode(Map<String, Rule> map, ArrayList<Rule> rule,
			ArrayList<Object> output, int i, PrintStream ps) {

		int x = i;
		int n = output.size();

		for (; x < n; x++)
			if (output.get(x) == RULEE)
				break;

		int nl = rule.size();

		// System.out.println("number of elements "+nl);
		// for(int j=i+1;j<x;j++)System.out.println(output.get(j));

		// System.out.println("-------------------------");
		// System.out.println("in rule code");
		int num = 0;
		for (int k = 0; k < nl; k++) {
			Rule r = rule.get(k);
			if (r.notSeq()) {

				for (int j = i + 1; j < x; j++) {

					Object o = output.get(j);
					if (o == R_L)
						r.printLeft(ps);
					else if (o == R_R)
						r.printRight(ps);
					else if (o == B_NUM)
						r.printNum(ps);
					else if (o == DEP_S) {
						j = r.loopReq(output, j, n, ps, map);
					} else
						code(ps, o, num);
				}
				num++;
			}
		}

		// System.out.println("end rule code");
		// System.out.println("----------------finished-------------------");

		return x;
	}

	/** Adds a module.
	 * 
	 * @param nm the name of the module.
	 * @param cls the module class.
	 */
	public void addMod(String nm, String cls) {
		
		
		if (nm.equals("gui")) {
			iMod = cls;
			
			/*
			 * if(iMod!=null){ Assume following message from inheritance The sub
			 * agent interface overrides the super interface
			 * System.err.println("Warning: GUI already defined.");
			 * System.err.println(iMod+" will be used rather than "+cls); }
			 */
		}
		else mod.add(cls);
	}

}
