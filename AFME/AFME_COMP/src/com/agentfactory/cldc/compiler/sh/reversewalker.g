header{
package com.agentfactory.compiler.sh;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
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

class RevWalker extends TreeParser;
{
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
	
	/** walk list of hidden tokens in order, printing them out */
	private void dumpHidden(antlr.CommonHiddenStreamToken t) {
	  for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    out.print(t.getText());
	  }
	}
	
	private void bPrint(StringBuilder sb,AST p){
		if(sb==null)pr(p);
		else{
			sb.append(p.getText());
			pr(p,"");
		} 
	}


}
	walk: 
	 {dumpHidden(Main.filter.getInitialHiddenToken());
	 out.println("\nword BELIEF(?fos);");
	 }	
	(act|per|mod|imp|role|rule)* EOF;
	
	act: a:"ACTUATOR" {pr(a,"act"+a.getText().substring(8));} cls;
	
	per: a:"PERCEPTOR" {pr(a,"per"+a.getText().substring(9));} cls;
	
	mod: m:"LOAD_MODULE" {pr(m,"mod"+m.getText().substring(11));} fos[null] {out.print('=');} cls;

	imp: i:"IMPORT" {pr(i,"import");} cls;

	item[StringBuilder sb]: (NOT {pr(#NOT);})? belief[false,sb];
	
	belief[boolean b,StringBuilder sb]: bel:"BELIEF" {
	if(b)pr(bel);else pr(bel,"");
	}
	 fosbody[b,sb]  ;
	
	fosbody[boolean b,StringBuilder sb]: 
	OB { if(b)bPrint(sb,#OB);else pr(#OB,"");}
	 fos[sb] (COMMA {
	bPrint(sb,#COMMA);
	} fos[sb])* CB { if(b)bPrint(sb,#CB); else pr(#CB,"");
	};
	
	beliefs: item[null] (AMPER {pr(#AMPER,",");} item[null])*;
	
	rule: beliefs IMP {pr(#IMP,">");} (commit|belief[true,null]) SEMI {pr(#SEMI);}; 

	role: ROLE {pr(#ROLE,"role ");} fos[null] OCB {pr(#OCB);} (trigger|rule)* CCB {pr(#CCB);} ;
	
	trigger: t:"TRIGGER" {pr(t,"trigger");} item[null] SEMI {pr(#SEMI);};
	
	cls: fos[null] (DOT {pr(#DOT);} fos[null])* SEMI {pr(#SEMI);};
	
	fos[StringBuilder sb]: STRING {bPrint(sb,#STRING);}
	 | (IDENT {bPrint(sb,#IDENT);} (fosbody[true,sb])?) ;
	
	commit : c:"COMMIT"{pr(c,"");} OB 
	{
		pr(#OB,"");
		boolean b=false;
		StringBuilder sb=new StringBuilder();
	}
	fos[sb]
	{
		String s=sb.toString();
		
		if(!(s.equals("Self")||s.equals("?Self"))){
			b=true;
			out.print(s);
		}
	} 
	COMMA 
	{
	if(b)pr(#COMMA);
	else pr(#COMMA,"");
	sb.setLength(0);
	
	}
	fos[sb]{
		s=sb.toString();
		if(b)out.print(s);
		else if(!(s.equals("Now")||s.equals("?Now"))){
				b=true;
				out.print(s);
		}
		
	} 
	cm2:COMMA 
	{
	if(b)pr(cm2);
	else pr(cm2,"");
	sb.setLength(0);
	}
	
	item[sb]
	{ 
		s=sb.toString();
		if(b)out.print(s);
		else if(!(s.equals("true"))){
				b=true;
				out.print(s);
		}
		
	} 
	cm3:COMMA 
	{
		if(b)pr(cm3);
		else pr(cm3,"");
	}
	fos[null] 
	
	(cm4:COMMA {pr(cm4,"\'");}
	 fos[null]
	 cm5:COMMA {pr(cm5);}
	 
	 fos[null])?
	
	CB 
	{pr(#CB,"");}
	
	;
 
