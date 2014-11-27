header{
package com.agentfactory.compiler.sh;

import java.util.HashSet;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
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

class SHWalker extends TreeParser;
{
	HashSet<FOS>set=new HashSet<FOS>();
	StringBuilder wb=new StringBuilder();
	
	PrintStream out;
	File f;
	public void createFile(String fileName){
		
		try{
			out=new PrintStream(new FileOutputStream(f=new File(fileName)));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		try{
		System.out.print("Output File: ");
		System.out.println(f.getCanonicalFile().toString());
			out.flush();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	/** walk list of hidden tokens in order, printing them out */
	private void dumpHidden(antlr.CommonHiddenStreamToken t) {
	  for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    out.print(t.getText());
	  }
	}
	
	private void printWait(){
		out.print(wb.toString());
		wb.setLength(0);
	}
	
	
	
	private void wait(AST p){
		antlr.CommonHiddenStreamToken t=((antlr.CommonASTWithHiddenTokens)p).getHiddenAfter();
		for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    	wb.append(t.getText());
	  	}
	}
	
	
	private void add(String s){
		if(s.charAt(0)=='!')s=s.substring(1);
		set.add(FOS.createFOS(s));
	}
	
	
	private boolean commitCheck(String s){
		
		if(s.charAt(0)=='!'){
			s=s.substring(1);
		}
		FOS f=FOS.createFOS(s);
		for(FOS fos:set){
			if(f.matches(fos)){
				return true;
				}
		}
		return false;
	}
	
	
	private void ck(String s){
		if(s.charAt(0)=='!'){
			s=s.substring(1);
			out.print('!');
		}
		
		FOS f=FOS.createFOS(s);
		for(FOS fos:set){
			if(f.matches(fos)){
				out.print(s);
				return;
				}
		}
		
		out.print("BELIEF("+s+')');
	}
	
	private void check(String s){
		
		ck(s);
		printWait();
	}
	
	
	
	private void dump(AST p){
		dumpHidden(
			((antlr.CommonASTWithHiddenTokens)p).getHiddenAfter()
		);
	}
	
	private void pr(AST p,String s){
		out.print(s);
		dump(p);
	}
	
	private void pr(AST p) {
		pr(p,p.getText());
	}
}
	walk:
	 {dumpHidden(Main.filter.getInitialHiddenToken());}	
	 (rule|act|per|word|mod|imp|role|ESC { 
	 String s=#ESC.getText();
	 pr(#ESC,s.substring(2,s.length()-1));
	 })*;
	
	act: a:ACT {pr(a,"ACTUATOR"+a.getText().substring(3));}  cls (COMMA cls {System.out.println("comma actuator feature not supported");} )* SEMI {pr(#SEMI);};
	
	per: a:PER {pr(a,"PERCEPTOR"+a.getText().substring(3));} cls  (COMMA cls {System.out.println("comma perceptor feature not supported");} )* SEMI {pr(#SEMI);};
	
	mod: m:MOD {pr(m,"LOAD_MODULE"+m.getText().substring(3));} fos[null] (DOT {pr(#DOT);} fos[null])* eq:EQUALS {pr(eq," ");} cls SEMI {pr(#SEMI);};
	
	word{StringBuilder sb=new StringBuilder();}:  WORD {pr(#WORD,"");} fos[sb] {add(sb.toString()); printWait();
	} SEMI {pr(#SEMI,"");};
	
	imp: i:"import" {pr(i,"IMPORT");} cls  (COMMA cls {System.out.println("comma import feature not supported");} )* SEMI {pr(#SEMI);};

	beliefs: {StringBuilder sb=new StringBuilder();} fos[sb] { check(sb.toString()); }
		(c:COMMA {sb.setLength(0); pr(c,"&");} fos[sb] {check(sb.toString());} )*;	
	
	commafos[StringBuilder sb]:  fos[sb] (COMMA {
	if(sb==null)pr(#COMMA);
	else{
		sb.append(#COMMA.getText());
		wait(#COMMA);
	}
	
	} fos[sb])* ;
	
	rule: (beliefs)? ((IS {System.out.println("Belief labeling not supported");}fos[new StringBuilder()]) |(IMP {pr(#IMP,"=>");} (commit))) (REQ {System.out.println("Belief labeling not supported");} commafos[new StringBuilder()] {System.out.println("requires not supported");} )? SEMI {pr(#SEMI);}; 

	role: r:ROLE {pr(r,"ROLE"+r.getText().substring(4));} fos[null] OCB 
	{pr(#OCB);}(trigger|rule|ESC)* CCB {pr(#CCB);};
	
	trigger{StringBuilder sb=new StringBuilder();}: t:"trigger" {pr(t, "TRIGGER");} 
	fos[sb] {check(sb.toString());} SEMI {pr(#SEMI);};
	
	cls: fos[null] (DOT {pr(#DOT);} fos[null])* ;

	fos[StringBuilder sb]: (NOT {if(sb==null)pr(#NOT);else {
	pr(#NOT,"");sb.append(#NOT.getText());
	}
	})? 
	( (STRING {if(sb==null)pr(#STRING);else{
	 wait(#STRING); sb.append(#STRING.getText());
	 }
	 } ) |
	 ((INT {if(sb==null)pr(#INT);else{
	 wait(#INT);
	 sb.append(#INT.getText());
	 }
	 } |
	 IDENT {if(sb==null)pr(#IDENT);else{
	 
	 wait(#IDENT);
	 sb.append(#IDENT.getText());
	 }
	 } )+ (OB {if(sb==null)pr(#OB);else
	 { 
	 wait(#OB);
	 sb.append(#OB.getText());
	 }} 
	 commafos[sb] CB {if(sb==null)pr(#CB);else{
	 wait(#CB);
	 sb.append(#CB.getText());
	 }
	 } )?)) ;
	 
	 

	commit{
	StringBuilder sb=new StringBuilder();
	String s1=null,s2=null,s3=null,s4=null;
	String m1=null,m2=null,m3=null,m4=null;
	} :
 	(fos[sb] {
 	s1=sb.toString();
 	m1=wb.toString();
 	wb.setLength(0);
 	
 	}
 	(COMMA {wait(#COMMA);
 		sb.setLength(0);
 		m1=m1+#COMMA.getText();
 	
 		} fos[sb] {
 		s2=sb.toString();
 		m2=wb.toString();
 		
 		wb.setLength(0);
 		}
 		(COMMA {wait(#COMMA);
 		sb.setLength(0);
 		m2=m2+#COMMA.getText();
 	
 		} fos[sb] {
 		s3=sb.toString();
 		m3=wb.toString();
 		wb.setLength(0);
 		}
 		(COMMA  {wait(#COMMA);
 		sb.setLength(0);
 		m3=m3+#COMMA.getText();
 		} 
 		fos[sb]
 		{
 		s4=sb.toString();
 		m4=wb.toString();
 		wb.setLength(0);
 		}
 	
 	)? )? )? )
 	
 	{
 		if(s2==null){
 			if(commitCheck(s1)){
 				out.print(s1+m1);
 			}
 			else {
 				out.print("COMMIT(?Self,?Now,BELIEF(true),"+s1+m1);
 			}
 		}else if(s3==null){
 			out.print("COMMIT(Self,Now,");
 			ck(s1);
 			out.print(m1+s2+m2);
 			
 		}else if(s4==null){
 			out.print("COMMIT(Self,"+s1+m1);
 			ck(s2);
 			out.print(m2+s3+m3);
 		}else{
 			out.print("COMMIT("+s1+m1+s2+m2);
 			ck(s3);
 			out.print(m3+s4+m4);
 		}
 	}
 	
 	(APOS {pr(#APOS,",");}
 	fos[null] 
 	COMMA {pr(#COMMA);}
 	fos[null] (COMMA fos[null]{System.out.println("commitment ids not supported");})?)?

 	{
 		out.print(')');
 	}
 	;
	
	
 
	