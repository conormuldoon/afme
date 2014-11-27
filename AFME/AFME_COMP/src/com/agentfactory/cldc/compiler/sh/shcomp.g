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

class SHCompParser extends Parser;

options {
	buildAST = true;
	}

	shapl: (rule|act|per|word|mod|imp|role|ESC)* ;
	
	act: ACT cls (COMMA cls)* SEMI;
	
	per: PER cls (COMMA cls)* SEMI;
	
	mod: MOD fos (DOT fos)* EQUALS cls SEMI;
	
	word: WORD fos SEMI;
	
	imp: "import" cls(COMMA cls)* SEMI;
	
	commafos: fos (COMMA fos)* ;
	
	rule: (commafos)? ((IS fos)|(IMP (commit))) (REQ commafos )? SEMI; 

	role: ROLE fos OCB (trigger|rule|ESC)* CCB ;
	
	trigger: "trigger" fos SEMI;
	
	cls: fos (DOT fos)*;

	fos: (NOT)? ( (STRING) | ((INT|IDENT)+ (OB commafos CB)?)) ;

	commit :
 	(fos (COMMA fos (COMMA fos (COMMA fos)? )? )? ) (APOS fos COMMA fos (COMMA fos)?)?;
 

class SHCompLexer extends Lexer;
options {
    k=6;
}

{
public void tab() {
	int t = 4;
	int c = getColumn();
	int nc = (((c-1)/t)+1)*t+1;
	setColumn( nc );
}
}


REQ options {
  paraphrase = "a requires clause (req)";
}: "req" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');
	

IS options {
paraphrase = "a belief label (is)";
}: "is" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');

APOS options {
  paraphrase = "an apostrophe";
}: '\'';

ROLE options {
  paraphrase = "a role";
}: "role" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');

ACT options {
  paraphrase = "an actuator";
}: "act" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');
	
PER options {
  paraphrase = "a perceptor";
}: "per" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');
	
MOD options {
  paraphrase = "a module";
}: "mod" (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');
	
WORD options{
 paraphrase = "a word";
 }: "word"  (' ' | '\r' '\n' {newline();}| '\n' {newline();}| '\t');


NOT options{
	paraphrase= "a negation";
	}: '!' ;

EQUALS options {
  paraphrase = "an equals sign";
}: '=';

INT options {
  paraphrase = "an integer";
}: ('0'..'9')+;


IDENT
options {
			testLiterals=true;
            paraphrase="a first order structure or an identifier";
        }
: 
('?'|'a'..'z'|'A'..'Z'|'_'|'$'|'+'|'-')+ ;



STRING options {
  paraphrase = "a string";
}
: '"' (('\\' ('\\'|'"')) | ~('\\'|'"'))* '"' ;


ESC options {
  testLiterals=true;
  paraphrase = "an escape sequence";
}
: '#' '[' (('\\' ('\\'|']')) | ~('\\'|']'))* ']' ;


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
}: '>' ;

SEMI options {
  paraphrase = "a semicolon";
}: ';' ;

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

	