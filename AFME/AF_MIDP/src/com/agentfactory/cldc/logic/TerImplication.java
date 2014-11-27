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



/** TerImplication objects represent commitment rules. A commitment rule comprises a belief
 * sentence, a commitment, and resource constraints.
 * 
 * @author Conor Muldoon
 */
public class TerImplication {

	FOS right;

	BelSeq seq;
	private final static char IMP='>';
	private final static char DELIM=',';
	public final static char APOS='\'';
	String v,c,id;
	
	/** Constructs a TerImplication from the specified string representation of the commitment and
	 * belief sequence. If no resource constraints are represented in the string,
	 * default resource constraints of 1 for value and 0 for cost are used. That is,
	 * any time the belief sentence of the rule is evaluated to true the commitment
	 * will be adopted provided the resource constraints have not been altered.
	 * 
	 * @param string a string representation of the commitment.
	 * @param bs the specified belief sequence.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	
	public TerImplication(String string,BelSeq bs) throws MalformedLogicException {

		//System.out.println("creating "+string);
		//int index = string.indexOf(IMP);
		seq=bs;
		//seq = new BelSeq(string.substring(0, index), null, 0);
		int aind=string.indexOf(APOS);
		String r=null;
		int num=string.charAt(string.length()-1)-'0';
		id=null;
		if(aind==-1){
			
			v="1";
			c="0";
			r=string.substring(0,string.length()-1);
			
		}
		else{
			r=string.substring(0,aind);
			int n=string.length()-1;
			StringBuffer sb=new StringBuffer();
			int i=aind+1;
			for(;string.charAt(i)!=DELIM;i++){
				
				sb.append(string.charAt(i));
			}
			v=sb.toString();
			String temp=string.substring(i+1,n);
			
			int index=temp.indexOf(DELIM);
			if(index==-1)c=temp;
			else {
				c=temp.substring(0,index);
				
				id=temp.substring(index+1);
				//System.out.println("id: "+id);
			}
			
		}
		int i=r.length();
		
		String s[] = new String[4];
		int k=0;
		
		for(int j=0;j<num;j++){
			StringBuffer sb=new StringBuffer();
			int b=0;

			loop:for(;k<i;k++){
				
				char c=r.charAt(k);
				if(c==FOS.OPEN_BRACKET)b++;
				else if(c==FOS.CLOSE_BRACKET)b--;
				if(c==DELIM&&b==0){
					k++;
					break loop;
				}
				sb.append(c);	
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
		//System.out.println(v);
		//System.out.println(c);
		//System.out.println(sb);
		
		right = FOS.createFOS(sb.toString());
	}
	/** Clears the belief sequence of previously stored data.
	 *
	 */
	public void seqClear(){
		seq.seqClear();
	}

	private TerImplication(BelSeq bSeq, FOS r) {
		seq = bSeq;
		right = r;

	}

	/** Creates a commitment for each binding in which the belief
	 * sentence of the rule evaluates to true.
	 * 
	 * @param agent the agent that the commitment rule belongs to.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public void solve(Agent agent) throws MalformedLogicException {
		
		if(seq.solveSeq(agent))seq.createCommit(right,agent);
		
		//seq.constructSol();
		
		seq.clear();
	}

	/** Creates a TerImplication by applying the substitution set to the belief sequence and 
	 * commitment of the rule.
	 * 
	 * @param sub the specified substitution set.
	 * @return the created rule.
	 * @throws MalformedLogicException if there is a logic error.
	 */
	public TerImplication createAppliedRule(SubstitutionSet sub)
			throws MalformedLogicException {
		BelSeq bSeq=seq.createAppliedSeq(sub);
		FOS fos = right.apply(sub);
		return new TerImplication(bSeq, fos);
	}
/*
	public void printState(){
		seq.printState();
	}
	*/
	/** Appends a string representation of the commitment to the specified
	 * string buffer.
	 * @param buffer the specified string buffer.
	 */
	public void append(StringBuffer buffer) {
		seq.append(buffer);
		buffer.append(' ');
		buffer.append(IMP);
		buffer.append(' ');
		right.append(buffer);
	}

}
