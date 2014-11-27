
 
header{
package com.agentfactory.cldc.compiler;
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
 
class AFMEParser extends Parser;
options {
    k=2;
    buildAST = true;   // uses CommonAST by default
}    


    begin : pkg (gui)? name LCURL /*(resources)?*/ scheduler (service|module)* (create)* (add)* 
      (start)* (template)+ RCURL ;
	
	pkg: "package" IDENT SEMI;
	
	//resources: "resources" IDENT SEMI;
	
	name: "platform" IDENT ;
	
	gui: "gui" IDENT SEMI;
	
	file: IDENT (SEP IDENT)* ;
	
	//gui: "gui" IDENT SEMI;
	//inter: "gui" SEMI;
	
    scheduler: "scheduler" fos SEMI;

   	create: "create" IDENT file fos (fos)? SEMI;
   	
   	add: ("add" IDENT|"addall") belief (COMMA belief)* SEMI;
  
  	service: "service" IDENT (fos)* SEMI;
    
    
    module: "module" IDENT IDENT SEMI;
  	
  	belief: ("always" LPAR belief RPAR )|("next" LPAR belief RPAR)| ( fos );
  	
  	fos: STRING | (IDENT (LPAR fos (COMMA fos)* RPAR)?) ;
  	
  	start: "start" IDENT (COMMA IDENT)* SEMI;
    
    template: "template" file IDENT (file IDENT)* SEMI;
    


class AFMELexer extends Lexer;

options {
    k=4;
}


SEP options{
	paraphrase = "a file separator";
	}: '\\' | '/';
	



STRING options {
  paraphrase = "a string";
}
: '"'! (('\\'! ('\\'|'"')) | ~('\\'|'"'))* '"'! ;

 
WS    : ( ' '
        | '\r' '\n' {newline();}
        | '\n' {newline();}
        | '\t'
        )
        {$setType(Token.SKIP);}
      ;
      





LCURL options {
  paraphrase = "a left curly bracket";
} 
: '{';

RCURL options{
  paraphrase = "a right curly bracket";
}
: '}';


LPAR options {
  paraphrase = "left parenthesis";
} 
: '(';

RPAR options{
  paraphrase = "right parenthesis";
}
: ')';


COMMA
options {
  paraphrase = "a comma";
} 
: ',' ;


// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n' |'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

// multiple-line comments
ML_COMMENT
	:	"/*"
		(	
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{$setType(Token.SKIP);}
	;


IDENT
options {
			testLiterals=true;
            paraphrase="a FOS or an identifier";
        }
: 
('.'|':'|'*'|'?'|'a'..'z'|'A'..'Z'|'_'|'$'|'+'|'-'|'0'..'9')+ ;


SEMI 
options {
  paraphrase = "a semicolon";
}
: ';' ;