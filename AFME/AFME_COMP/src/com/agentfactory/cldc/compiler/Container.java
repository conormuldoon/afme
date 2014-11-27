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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import antlr.CommonASTWithHiddenTokens;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.collections.AST;

import com.agentfactory.compiler.sh.SHCompLexer;
import com.agentfactory.compiler.sh.SHCompParser;

/** The Container class stores information relevant to the agent platform
 * script. It is used in the generation of output files.
 * 
 * @author Conor Muldoon
 * 
 */
public class Container {

	public static final Integer BLR = 0;

	public static final Integer BF = 1;

	public static final Integer LOOP = 2;

	public static final Integer DN = 3;

	public static final Integer NUMR = 4;

	// public static final Integer RS = 5;

	// public static final Integer RE = 6;

	// public static final Integer RL = 7;

	public static final Integer TS = 8;

	public static final Integer TE = 9;

	public static final Integer TR = 10;

	public static final Integer CLS = 11;

	public static final Integer PS = 12;

	public static final Integer PE = 13;

	public static final Integer AS = 14;

	public static final Integer AE = 15;

	public static final Integer ARS = 16;

	public static final Integer ARE = 17;

	public static final Integer AR = 18;

	public static final Integer NARG = 19;

	// public static final Integer RTM = 20;

	public static final Integer ANM = 21;

	public static final Integer DNM = 22;

	public static final Integer NTR = 23;

	public static final Integer IDT = 24;

	public static final Integer RT = 25;

	public static final Integer NMA = 26;

	public static final Integer NMP = 27;

	public static final Integer RLS = 28;

	public static final Integer RLE = 29;

	public static final Integer NM = 30;

	public static final Integer MS = 31;

	public static final Integer ME = 32;

	public static final Integer IS = 33;

	public static final Integer IE = 34;

	public static final Integer RULE_S = 35;

	public static final Integer RULEE = 36;

	// public static final Integer R=37;
	public static final Integer R_R = 38;

	public static final Integer R_L = 39;

	// public static final Integer R_REQ=40;
	public static final Integer DEP_S = 41;

	public static final Integer DEP_L = 42;

	public static final Integer DEP_E = 43;

	public static final Integer B_NUM = 44;

	public static final Integer SEQ_S = 45;

	public static final Integer SEQ_E = 46;

	// public static final Integer SEQ_LAB=47;
	// public static final Integer SEQ_DEP=48;
	public static final Integer BL_V = 47;
	
	public static final Integer RESO= 48;

	public static final Integer COMM_S=49;
	public static final Integer INI_C=50;
	public static final Integer COMM_E =51;
	
	public static final String DELM = "_";

	boolean debug;

	int threads;

	Collection<Service> service;

	Collection<Agent> agent;

	Collection<Module> mod;

	ArrayList<Object> output;

	PrintStream ps;

	String name, pack, gui;

	URLClassLoader loader;
	boolean p;

	/** Creates an instance of Container.
	 * 
	 * @param l the class loader.
	 * @param pkg the package that the platform belongs to.
	 * @param g the GUI.
	 * @param n the name of the platform.
	 * @param num the number of threads on the platform.
	 * @param prime a flag that indicates whether to generate prime numbers for the response times.
	 */
	public Container(URLClassLoader l, String pkg, String g, String n, int num,boolean prime) {
		p=prime;
		loader = l;
		name = n;
		threads = num;
		gui = g;
		pack = pkg;
		service = new ArrayList<Service>();
		agent = new ArrayList<Agent>();
		mod = new ArrayList<Module>();
		output = new ArrayList<Object>();
		
	}

	private class Service {
		String cls;

		ArrayList<String> args;

	}
	

	private class Agent {
		String name;

		String file;

		int rTime;
		int res;
		boolean start;

		Collection<String> belief;

		public Agent(String n, String f, int t,int r) {
			name = n.replaceAll("\\.", DELM);
			file = f;
			rTime = t;
			res=r;
			start = false;
			belief = new ArrayList<String>();
		}

		public boolean equals(Object o) {
			return name.equals(((Agent) o).name);
		}

	}

	private class Module {
		String name, cls;
	}

	/** This output applies the contents of the container to the specified
	 * template and generates an output file.
	 * 
	 * @param s the platform template.
	 * @param outFile the output file name.
	 */
	public void platApply(String s, String outFile) {
		// to do: note plat value

		System.out.println("Processing: " + s);
		try {

			InputStream is = null;
			if (loader != null){
				is = loader.getResourceAsStream(s);
				
			}
			else{
				try{
					is = new FileInputStream(s);
					
				}catch(FileNotFoundException e){
					
					ClassLoader cl=ClassLoader.getSystemClassLoader();
					
					is=cl.getResourceAsStream(s);
					
				}
			}
			TemplateLexer lex = new TemplateLexer(is);
			TemplateParser parse = new TemplateParser(lex);

			parse.start();
			AST t = parse.getAST();
			TemplateWalker walker = new TemplateWalker();
			
			File out = new File(pack.replace(".", "/") + '/' + outFile
					+ ".java");
			
			ps = new PrintStream(out);
			
			walker.walk(t, this, ps);
			
			ps.close();
			
			System.out.println("Output File: "
					+ out.getCanonicalPath().toString());
			// CommonAST t = (CommonAST) parse.getAST();
			// AFMEWalker walker = new AFMEWalker();

			// walker.begin(t);

			System.out.println("Completed: " + s);

		} catch (FileNotFoundException e) {
			System.out.println("Error file not found: " + e.getMessage());
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {

			e.printStackTrace();
		} catch (RecognitionException re) {
			System.err.println("Bad input: " + re);
		}

	}

	/** Writes the package name to the print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void pack(PrintStream ps) {
		ps.print(pack);
	}

	/** Writes the platform name to the specified print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void plat(PrintStream ps) {
		ps.print(name);
	}

	/** Writes the number of agents to the specified print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void numAgt(PrintStream ps) {
		ps.print(agent.size());
	}

	/** Writes the number of threads to the print stream.
	 * 
	 * @param ps the specified print stream.
	 */
	public void numT(PrintStream ps) {
		ps.print(threads);
	}

	/** 
	 * Writes services to the internal print stream.
	 */
	public void outServ() {

		int sn = service.size();
		Iterator<Service> sit = service.iterator();
		int n = output.size();

		for (int i = 0; i < sn; i++) {
			Service s = sit.next();
			for (int j = 0; j < n; j++) {
				Object o = output.get(j);
				if (o == NARG) {
					ps.print(s.args.size());
				} else if (o == ARS) {

					j = loopCode(s.args, output, j, ps, ARE, AR);

				} else if (o == CLS)
					ps.print(s.cls);
				else
					code(o, i);
			}
		}

		output.clear();

	}

	/** Creates expanded output for a loop of tokens.
	 * 
	 * @param list a list of tokens.
	 * @param output the output list.
	 * @param i the start index of the loop.
	 * @param ps the specified print stream.
	 * @param endCond the end condition.
	 * @param placeHold a place holder.
	 * @return the end index of the loop.
	 */
	public static int loopCode(ArrayList<String> list,
			ArrayList<Object> output, int i, PrintStream ps, Object endCond,
			Object placeHold) {
		int x = i;
		int n = output.size();

		for (; x < n; x++)
			if (output.get(x) == endCond)
				break;

		int nl = list.size();

		// System.out.println("number of elements "+nl);
		// for(int j=i+1;j<x;j++)System.out.println(output.get(j));

		// System.out.println("-------------------------");
		for (int k = 0; k < nl; k++) {
			String s = list.get(k);
			for (int j = i + 1; j < x; j++) {
				Object o = output.get(j);
				if (o == placeHold)
					ps.print(s);
				else
					code(ps, o, k);
			}
		}
		// System.out.println("----------------finished-------------------");

		return x;
	}

	/**
	 * Writes output for the agent design files.
	 */
	public void outDes() {

		Collection<String> c = new HashSet<String>();
			
			for (Agent a : agent)
				if (c.contains(a.file))
					continue;
				else {
					c.add(a.file);

					// Use ByteArrayInputStream or something like that
					// if ends with afapl2 convert to sh
					// Do parsing on sh / return Design Object
					// Error message: unrecognised extension .agent
					// Do not use compiled code? Map this !!!
					// Perhaps only accept short hand (simplify)
					// let the conversion be done manually

					Design d = new Design(loader);
					constructDesign(d, a.file, loader);
					try {
						d.sortSeq();
						d.process(ps, output, a.file);
					} catch (LabelException e) {
						System.err.println("Error in Design: " + a.file);
						System.err.println("Label Exception");
						e.printStackTrace();
					}catch(CycleException e){
						System.err.println("Error in Design: " + a.file);
						System.err.println("Cycle Exception");
						e.printStackTrace();
					}

				}
			output.clear();
		

	}

	/** Parses agent design files and adds information to the specified 
	 * Design object.
	 * 
	 * @param d the specified Design object.
	 * @param fileName the file name of the design.
	 * @param loader the specified class loader.
	 */
	public static void constructDesign(Design d, String fileName,
			URLClassLoader loader) {
		try {
			
			fileName = fileName.replaceAll("\\.", "/") + ".sh";
			
			System.out.println("Design File: " + fileName);
			java.io.InputStream is = null;
			
			if (loader != null)
				is = loader.getResourceAsStream(fileName);
			
			if (is == null)
				is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
			
			if (is == null)
				is = new FileInputStream(fileName);
			

			SHCompLexer lex = new SHCompLexer(is);

			lex.setTokenObjectClass("antlr.CommonHiddenStreamToken");
			TokenStreamHiddenTokenFilter filter = new TokenStreamHiddenTokenFilter(
					lex);
			filter.hide(SHCompParser.WS);
			filter.hide(SHCompParser.SL_COMMENT);
			filter.hide(SHCompParser.ML_COMMENT);

			SHCompParser parse = new SHCompParser(filter);
			parse.setASTNodeClass("antlr.CommonASTWithHiddenTokens");

			parse.shapl();

			CommonASTWithHiddenTokens t = (CommonASTWithHiddenTokens) parse
					.getAST();

			DesignWalker walker = new DesignWalker();
			;
		
			walker.walk(t, d);

		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	/** Prints the agent designs to the output print stream.
	 * 
	 */
	public void outAgt() {

		int an = agent.size();

		Iterator<Agent> it = agent.iterator();
		int n = output.size();
		/*
		 * System.out.println("Buffer"); for(Object
		 * s:output)System.out.println(s); System.out.println("End Buffer");
		 * 
		 * System.out.println("Agent"); for(Agent a:agent)
		 * System.out.println(a.name); System.out.println("End Agent");
		 */
		if(p){
			
			int max=0;
		
			for(Agent a:agent){
				max=Math.max(max,a.rTime);
			}
			
			//System.out.println("Max: "+max);
			final int OVER=21;
			max+=OVER;
			
			boolean[] prime=new boolean[max];
			
			java.util.Arrays.fill(prime,true);
			prime[0]=false;
			prime[1]=false;
			int m=(int)Math.sqrt(max);

			for (int i=2; i<=m; i++)
			   if (prime[i])
			      for (int k=i*i; k<max; k+=i)
			         prime[k]=false;

			final int TRES=10;
			loop:for(Agent a:agent){
				if(prime[a.rTime])continue;
				for(int i=TRES;i>0;i--){
					if(a.rTime-i>-1&&prime[a.rTime-i]){
						a.rTime=a.rTime-i;
						continue loop;
					}
					else if(prime[a.rTime+i]){
						a.rTime=a.rTime+i;
						continue loop;
					}
					
					//System.out.println("checking "+(a.rTime-i)+" "+(a.rTime+i));
				}
				
					for(int i=a.rTime+TRES;i<a.rTime+OVER;i++){
						if(a.rTime-i>-1)if(prime[a.rTime-i])
							{
							a.rTime=a.rTime-i;
							continue loop;
							}
						else if(prime[a.rTime+i]){
							a.rTime=a.rTime+i;
							continue loop;
						}
					}
					
						for(int i=a.rTime-OVER;i-->0;)
							if(prime[i]){
								a.rTime=i;
								continue loop;
							}
					
				}
				
			}
			
		
		for (int i = 0; i < an; i++) {
			Agent a = it.next();

			for (int j = 0; j < n; j++) {
				Object o = output.get(j);
				if (o == ANM)
					ps.print(a.name);
				else if (o == DNM) {
					ps.print(a.file.replaceAll("\\.", "_"));
				} else if(o==RESO){
					ps.print(a.res);
				}else if (o == RT)
				
					ps.print(a.rTime);
				else
					code(o, i);
			}
		}

		output.clear();
	}

	/** Prints the GUI class to the output print stream.
	 * 
	 */
	public void outGUI() {
		if (gui != null) {
			for (Object o : output)
				if (o == CLS)
					ps.print(gui);
				else
					ps.print(o);
		}
		output.clear();
	}

	/** Prints the agents' beliefs to the output print stream.
	 * 
	 */
	public void outBel() {
		int an = agent.size();
		Iterator<Agent> it = agent.iterator();
		int n = output.size();
		int cnt = 0;
		for (int i = 0; i < an; i++) {
			Agent a = it.next();
			int nb = a.belief.size();
			Iterator<String> bit = a.belief.iterator();
			for (int j = 0; j < nb; j++) {
				String bel = bit.next();
				for (int k = 0; k < n; k++) {
					Object o = output.get(k);
					if (o == BLR) {
						ps.print(a.name);
					} else if (o == BF)
						ps.print(bel);
					else
						code(o, cnt);

				}
				cnt++;
			}
		}
		output.clear();
	}

	/** Prints the starting agent names to the output print stream.
	 * 
	 */
	public void outStart() {

		int an = agent.size();
		Iterator<Agent> it = agent.iterator();
		int n = output.size();
		int cnt = 0;
		for (int i = 0; i < an; i++) {
			Agent a = it.next();

			if (a.start) {

				for (int j = 0; j < n; j++) {
					Object o = output.get(j);
					if (o == ANM)
						ps.print(a.name);
					else
						code(o, cnt);

				}
				cnt++;
			}
		}
		output.clear();
	}

	/** Prints the specified index if the object is the loop value, other wise prints the object.
	 * 
	 * @param ps the print stream.
	 * @param o the specified object.
	 * @param i the index.
	 */
	public static void code(PrintStream ps, Object o, int i) {
		if (o == LOOP) {

			ps.print(i);
		} else {
			ps.print(o);
		}
	}

	private void code(Object o, int i) {
		code(ps, o, i);
	}

	/** Adds a service to the container.
	 * 
	 * @param cls the class name of the service.
	 * @param args the arguments to the service.
	 */
	public void addService(String cls, ArrayList<String> args) {
		Service s = new Service();
		s.args = args;
		s.cls = cls;
		service.add(s);
	}

	/** Adds a module to the container.
	 * 
	 * @param cls the class of the module.
	 * @param name the name of the module.
	 */
	public void addModule(String cls, String name) {
		Module m = new Module();
		m.cls = cls;
		m.name = name;
		mod.add(m);
	}

	/** Adds an agent to the container.
	 * 
	 * @param s the name of the agent.
	 * @param f the file name of the agent design.
	 * @param time the response time of the agents.
	 * @param res the amount of resource available to the agent.
	 * @return false if the agent list contains the name, false otherwise.
	 */
	public boolean addAgent(String s, String f, int time,int res) {

		if (agent.contains(s))
			return false;
		agent.add(new Agent(s, f, time,res));
		return true;

	}

	/** Adds an item to the output list.
	 * 
	 * @param o the specified item.
	 */
	public void addItem(Object o) {
		output.add(o);
	}

	/** Adds a code segment to the output list.
	 * 
	 * @param t the specified code segment.
	 */
	public void addCode(AST t) {

		output.add(t.getText());
	}

	/** Adds a belief for a specific agent to the container.
	 * 
	 * @param s the name of the agent the belief is to be added to.
	 * @param bel the specified belief.
	 * @return
	 */
	public boolean addBelief(String s, String bel) {
		if(agent.size()==0)return false;
		if(s==null){
			for(Agent a:agent)a.belief.add(bel);
			return true;
		}
		for (Agent a : agent)
			if (a.name.equals(s)) {
				a.belief.add(bel);
				return true;
			}
		return false;
	}

	/** Adds an agent that is to be started when the platform begins to
	 * operate.
	 * 
	 * @param s the name of the agent.
	 * @return false if the agent is not on the platform or the name is spelt incorrectly, true otherwise.
	 */
	public boolean addStart(String s) {

		boolean b = false;
		for (Agent a : agent)
			if (a.name.equals(s)) {
				a.start = true;
				b = true;
			}
		if (b == false) {
			System.err.println("Problem with starting: " + s);
			System.err.println("Check if correct name used. ");
		}
		return b;
	}

}
