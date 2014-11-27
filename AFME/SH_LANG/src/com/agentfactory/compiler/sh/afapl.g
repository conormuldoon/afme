header{
package com.agentfactory.compiler.sh;
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

class AFAPLParser extends Parser;
options {
	buildAST = true;
	}
	
	reverse: (act|per|mod|imp|role|rule)* EOF;
	
	act: "ACTUATOR" cls;
	
	per: "PERCEPTOR" cls;
	
	mod: "LOAD_MODULE" fos cls;

	imp: "IMPORT" cls;

	item: (NOT)? belief;
	
	belief: "BELIEF" fosbody  ;
	
	fosbody: OB fos (COMMA fos)* CB;
	
	beliefs: item (AMPER item)*;
	
	rule: beliefs IMP (commit|belief) SEMI; 
	
	role: ROLE fos OCB (trigger|rule)* CCB ;
	
	trigger: "TRIGGER" belief SEMI;
	
	cls: fos (DOT fos)* SEMI;
	
	fos: STRING | (IDENT (fosbody)?) ;
	
	commit : "COMMIT" OB fos COMMA fos COMMA item COMMA fos (COMMA fos COMMA fos)? CB;
 

class AFAPLLexer extends Lexer;

options {
    k=7;
}

{
public void tab() {
	int t = 4;
	int c = getColumn();
	int nc = (((c-1)/t)+1)*t+1;
	setColumn( nc );
}
}

AMPER options{
	paraphrase="an ampersand";
	}: '&';
	
ROLE options {
  paraphrase = "a role";
}: "ROLE" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');


IDENT
options {
			testLiterals=true;
            paraphrase="a FOS or an identifier";
        }
: 
('?'|'a'..'z'|'A'..'Z'|'_'|'$'|'+'|'-'|'0'..'9')+ ;


STRING options {
  paraphrase = "a string";
}
: '"'! (('\\'! ('\\'|'"')) | ~('\\'|'"'))* '"'! ;

OCB options {
  paraphrase = "an opening curly bracket";
}: '{';

CCB options {
  paraphrase = "a closing curly bracket";
}: '}';

OB options {
  paraphrase = "an opening bracket";
}: '(';

CB options {
  paraphrase = "a closing bracket";
}: ')';



COMMA options {
  paraphrase = "a comma";
}: ',';

IMP options {
  paraphrase = "an implication";
}: '=' '>' ;

SEMI options {
  paraphrase = "a semicolon";
}: ';' ;

NOT options {
  paraphrase = "a negation";
}   : '!' ;

DOT  options {
  paraphrase = "a dot";
}   : '.' ;


// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{ newline();}
	;

WS    : ( ' '
        | '\r' '\n' {newline();}
        | '\n' {newline();}
        | '\t'
        )
        
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
		
	;
	


