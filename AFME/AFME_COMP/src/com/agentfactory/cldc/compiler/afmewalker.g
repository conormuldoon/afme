header{
package com.agentfactory.cldc.compiler;
import java.util.ArrayList;
import java.util.Collection;
import java.net.URLClassLoader;
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
 
 // Note: Debug no longer supported (remove later)
 
class AFMEWalker extends TreeParser;
   
   {
   private void append(StringBuilder sb,AST p){
   		sb.append(p.getText());
   }
   }

   begin[URLClassLoader loader,boolean p] : {StringBuilder builder=new StringBuilder();} pkg[builder] {String g=null;} (g=gui)? {StringBuilder sb=new StringBuilder(); } name[sb] LCURL 
 
   
   {StringBuilder sb1=new StringBuilder();}
   scheduler[sb1] {
   Container c=new Container(loader,builder.toString(),g,sb.toString(),Integer.parseInt(sb1.toString()),p);
   
    } (service[c]|module[c])* (create[c])* (add[c])* 
  
     (start[c])* (template[c])+ RCURL ;
	

	
	pkg[StringBuilder sb]: "package" IDENT {sb.append(#IDENT.getText());}SEMI;
	
	name[StringBuilder sb]: "name" IDENT {append(sb,#IDENT);} ;
	
	gui returns [String s=null]: "gui" IDENT SEMI {s=#IDENT.getText();};
	
	file[StringBuilder sb]: IDENT {append(sb,#IDENT);} (SEP id:IDENT {
	append(sb,#SEP);
	append(sb,id);
	})* ;
	
    scheduler[StringBuilder sb]: "scheduler" fos[sb] SEMI;

   	create[Container c]: "create" IDENT {StringBuilder sb=new StringBuilder();StringBuilder sb2=new StringBuilder();} 
   	file[sb] fos[sb2] {StringBuilder res=new StringBuilder("0");}
   	({res.setLength(0);}
   	fos[res])?
   	{c.addAgent(#IDENT.getText(),sb.toString(),
   	Integer.parseInt(sb2.toString()),
   	Integer.parseInt(res.toString())
   	);} SEMI;
   	
   	//gui returns[String s=null]: "gui" IDENT SEMI {s=#IDENT.getText();};
   	
   	inter: "gui" SEMI;
   	
   	add[Container c]:{String nm=null;} ("add" id:IDENT {nm=id.getText();}|"addall") {StringBuilder sb=new StringBuilder();} belief[sb] {c.addBelief(nm,sb.toString());}
   	(COMMA {sb.setLength(0);}
   	belief[sb] {c.addBelief(nm,sb.toString());})* SEMI;
  
  	service[Container c]: "service" {
  	StringBuilder sb=new StringBuilder();
  	ArrayList<String>cl=new ArrayList<String>();}
  	IDENT 
  	{
  	append(sb,#IDENT);
  	String cls=sb.toString();
  	}
  	({sb.setLength(0);} fos[sb] {cl.add(sb.toString());})*  
  	{
  		c.addService(cls,cl);
  	}
  	SEMI;
    
    
    module[Container c]: "module" IDENT id:IDENT {c.addModule(#IDENT.getText(),id.getText());}SEMI;
  	
  
  	
  	belief[StringBuilder sb]: 
  	(a:"always" LPAR {append(sb,a); append(sb,#LPAR);}
  	 belief[sb] RPAR {append(sb,#RPAR);}
  	  )|(n:"next" LPAR 
  	  {append(sb,n);append(sb,#LPAR);} 
  	  belief[sb] 
  	  RPAR {append(sb,#RPAR);})| (
  	  fos[sb] );
  	
  		fos[StringBuilder sb]: STRING {append(sb,#STRING);} |
  	 (IDENT {append(sb,#IDENT);}
  	  (LPAR {append(sb,#LPAR);} fos[sb] 
  	  (COMMA {append(sb,#COMMA);} fos[sb] )* 
  	  RPAR {append(sb,#RPAR);}
  	  )?) ;
  	
  	start[Container c]: "start" IDENT {c.addStart(#IDENT.getText());}
  	 (COMMA id:IDENT {c.addStart(id.getText());})* SEMI;
    
    template[Container c]: "template" tpt[c] (tpt[c])*
     SEMI;
     
     
     tpt[Container c]: {StringBuilder sb=new StringBuilder();
    			StringBuilder sb1=new StringBuilder();
    		}
    		file[sb] {String s=null;} IDENT {
   	
    c.platApply(sb.toString(),#IDENT.getText());
    };
    

    