header{
package com.agentfactory.cldc.compiler;

import java.util.Collection;
import java.util.ArrayList;
}

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
 
 /* Author: Conor Muldoon */

class DesignWalker extends TreeParser;
{
	private void append(StringBuilder sb,AST t){
		sb.append(t.getText());
	}
	static void secondArgs(String s,StringBuilder sb){
		String st[]=s.split("\\+");
		for(String str:st){
			String strg[]=str.split("-");
			for(String str2:strg){
				if(str2.equals(""))continue;
				sb.append(',');
				sb.append(str2);
				
			}
		}
		sb.append(')');
	}
	static void plusMin(String s,String plmn,StringBuilder sb){
		String[]st=s.substring(1).split("\\+");
		int cnt=0;
		String vals[]=new String[st.length*2];
		
		
		for(int i=0;i<st.length;i++){
			String str[]=st[i].split("-");
			for(int j=0;j<str.length;j++){
				if(str[j].equals(""))continue;
				vals[cnt++]=str[j];
			}
		}
		sb.append(cnt);
		sb.append(plmn);
		sb.append('(');
		sb.append(vals[0]);
		
		for(int i=1;i<cnt;i++){
			sb.append(',');
			sb.append(vals[i]);
		}
		
	}
	static void splitPlusMin(String s,String exp,String plmn,StringBuilder sb,char c){
		String[]st=s.split(exp);
		sb.append(c);
		plusMin(st[0],plmn,sb);
		secondArgs(st[1],sb);
	}
	static String pmString(String s,char c){
		StringBuilder sbpm=new StringBuilder();
		if(s.charAt(1)!='-'&&s.charAt(1)!='+')
			sbpm.append('+');
		
		int n=s.length();
		for(int i=0;i<n;i++){
			if(s.charAt(i)=='+')
			sbpm.append('+');
			else if(s.charAt(i)=='-')
			sbpm.append('-');
			else if(s.charAt(i)==c&&s.charAt(i+1)!='-'
				&&s.charAt(1)!='+')sbpm.append('+');
			
		}
		return sbpm.toString();
	}
	static String processExp(String s){
		
		s=s.replaceAll("[ \\n\\r\\t]*","");
		s=s.replaceAll("/\\*([^/])*\\*/","");
		s=s.replaceAll(">=","g");
		s=s.replaceAll("<=","l");

		StringBuilder sb=new StringBuilder("#");
		if(s.indexOf('=')>-1){
			//Check this
			if(s.indexOf('>')>-1){
				splitPlusMin(s,">[ ]*=",pmString(s,'g'),sb,'g');
				
			}else if(s.indexOf('<')>-1){
				splitPlusMin(s,"<[ ]*=",pmString(s,'l'),sb,'l');
			}else{	// Symbol = '='
				splitPlusMin(s,"=[ ]*=",pmString(s,'='),sb,'=');
			}
		}else if(s.indexOf('>')>-1){
			splitPlusMin(s,">",pmString(s,'>'),sb,'>');
			
		}else{	// Symbol= '<'
			splitPlusMin(s,"<",pmString(s,'<'),sb,'<');
		}
		return sb.toString();
	}
}

	walk [Design design]: 
	({StringBuilder sb=new StringBuilder();
	
	Rule r;}
	r=rule[design] {if(r!=null)design.addRule(r);}
	|act[design]|per[design]|word[design]|mod[design]|imp[design]|role[design]|
	es:ESC {design.addEs(es.getText());}
	
	)*;
	
	act[Design d]: ACT {StringBuilder sb=new StringBuilder();}
	cls[sb] {
	d.addAct(sb.toString());} (COMMA {sb.setLength(0);} cls[sb] {d.addAct(sb.toString());})* SEMI;
	
	per[Design d]: PER {StringBuilder sb=new StringBuilder();}
	cls[sb] {d.addPer(sb.toString());}
	(COMMA {sb.setLength(0);} cls[sb] {d.addPer(sb.toString());})*
	SEMI;
	
	mod[Design d]: MOD {StringBuilder sb=new StringBuilder();}
	fos[sb] 
	(DOT {append(sb,#DOT);} fos[sb]

	)* EQUALS {StringBuilder sb2=new StringBuilder();}
	cls[sb2] {d.addMod(sb.toString(),sb2.toString());} SEMI;
	
	word[Design d]: WORD {StringBuilder sb=new StringBuilder();}
	fos[sb] {d.addWord(sb.toString());} SEMI;
	
	imp[Design d]: "import" {StringBuilder sb=new StringBuilder();}
	cls[sb] {d.imp(sb.toString());}
	(COMMA {sb.setLength(0);} cls[sb] {d.imp(sb.toString());})*
	SEMI ;
	
	
	commaatom[StringBuilder sb]: fos[sb] 
	 | exp:EXP 
	{
	sb.append(processExp(exp.getText()));
	};
	
	commafos[StringBuilder sb]:  commaatom[sb] (COMMA {append(sb,#COMMA);} (commaatom[sb]))* ;
	
	
	// Fix me: Remove '|', '¦',and '¬' from following two rules, perform all parsing in compiler
	// template num beliefs, label (label might not be required, 
	// just create Bel[]arr) Will this work?
	// requires template. how to match?
	
	countfos[StringBuilder sb] :{int i=1;} commaatom[sb] (COMMA {i++;} {append(sb,#COMMA);}
	commaatom[sb])* {sb.append('|');sb.append(i);};
	
	rule[Design d] returns[Rule r=null]: {StringBuilder s1=new StringBuilder();
	StringBuilder s2=new StringBuilder();
	ArrayList<String>al=new ArrayList<String>();
	StringBuilder s3=new StringBuilder();
	StringBuilder s4=new StringBuilder();
	}
	 (countfos[s1])? ((res[s2])? {d.addInitialCommitment(s1.toString(),s2.toString());}|
	((IS  fos[s4])|(IMP 
	(commit[s2]))) (REQ  fos[s3] {al.add(s3.toString());} (COMMA {s3.setLength(0);}
	fos[s3] {al.add(s3.toString());})* )? 
	{
	r=new Rule(s1.toString(),s2.toString(),al,s4.toString());
	})
	SEMI; 

	role[Design d]: ROLE {StringBuilder sb=new StringBuilder();}
	fos[sb] {ArrayList<String>trig=new ArrayList<String>();
	ArrayList<Rule> r=new ArrayList<Rule>();Rule aRule;
	   }

	OCB (trigger[trig]|{StringBuilder rl =new StringBuilder();}
	 aRule=rule[d] {if(aRule!=null)r.add(aRule);}
	|esc:ESC {System.out.println("ESC not supported. "+esc.getText());})* CCB 
	{d.addRole(sb.toString(),r,trig);};
	
	trigger[Collection<String> t]: "trigger" {StringBuilder sb=new StringBuilder();}
	fos[sb] {t.add(sb.toString());}
	SEMI;
	
	cls[StringBuilder sb]: fos[sb] 
	 (DOT {append(sb,#DOT);} fos[sb])* ;

	fos[StringBuilder sb]: (NOT {append(sb,#NOT);})? 
	( (s:STRING {append(sb,s);}) | 
	((i:INT {append(sb,i);}
	|idt:IDENT {append(sb,idt);})+ 
	(OB {append(sb,#OB);}
	commafos[sb] CB
	{append(sb,#CB);}
	)?)) ;

	commit[StringBuilder sb] :{int i=1;}
 	(fos[sb]  (COMMA {append(sb,#COMMA);}
 	fos[sb] (COMMA {append(sb,#COMMA);} fos[sb] 
 	(COMMA {append(sb,#COMMA);} (fos[sb] | exp:EXP {sb.append(processExp(exp.getText()));})
 	 {i++;})? {i++;})? {i++;})? ) 
 	(res[sb])? {sb.append(i);};
 
 	res[StringBuilder sb] :APOS {append(sb,#APOS);} fos[sb] 
 	COMMA {append(sb,#COMMA);} fos[sb] (COMMA {append(sb,#COMMA);} fos[sb])?;
	