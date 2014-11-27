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
 
class TemplateParser extends Parser;
options {
    buildAST = true;   // uses CommonAST by default
}


start : (SL_CODE|
		ML_CODE|
		PACKAGE|
		NUMT|
		PLAT|
		NUMAGTS| 
		forall
		)*;

forall: ((beliefs)|(designs)|(services)|(gui)|(agents)|(starters));

beliefs: BELIEFS LPAR (BELIEF|BELIEVER|SL_CODE|ML_CODE|LCNT )* RBR;



designs: DESIGNS LPAR (DNAME|(ruleloop)|NRULES|NACT|NPER|NMOD|
(perloop)|(actloop)|(sequences)|(roleloop)|(modloop)|(intermod)|(initcomm)
|SL_CODE|ML_CODE|LCNT)* RBR;

sequences: SEQUENCES LPAR 
(depends|BEL_NUM|RULE_L|LABEL|ML_CODE|SL_CODE)* 
RBR;

services: SERVICES LPAR (NUMARGS|(argloop)|CLASSNAME|SL_CODE|ML_CODE|LCNT)* RBR;

agents: AGENTS  LPAR (( RTIME )|(RES)|
(AGTNAME )|(DNAME )
|(SL_CODE )|
(ML_CODE )
|(COUNTER ))* RBR;

starters: STARTERS LPAR (AGTNAME|SL_CODE|ML_CODE|LCNT)* RBR;

roleloop: ROLES LPAR (NRULES|ID|NTRIG|ruleloop|triggerloop|SL_CODE|ML_CODE|COUNTER)* RBR;

argloop: ARGS LPAR (ARG|SL_CODE|ML_CODE|COUNTER)* RBR;


ruleloop: RULES  LPAR (depends|BEL_NUM|RULE_L|RULE_R|SL_CODE|ML_CODE|COUNTER)* RBR;
triggerloop: TRIGGERS LPAR (TRIGGER|SL_CODE|ML_CODE|COUNTER)* RBR ;

initcomm: INITCOMM LPAR (SL_CODE|ML_CODE|COMMIT)* RBR;

depends: DEPENDS LPAR 
(LABEL|SL_CODE|ML_CODE|COUNTER|BOOL_VAL)* 
RBR ;
perloop: PERCEPTORS cnm;
actloop: ACTUATORS cnm;
modloop: MODULES cnm;
intermod: INTERMOD cnm;
gui: GUI cnm;
cnm: LPAR (CLASSNAME|SL_CODE|ML_CODE|COUNTER)* RBR;


class TemplateLexer extends Lexer;
options {
  k=5;
  
}
BOOL_VAL: "boolval";
STARTERS : "starters";
PERCEPTORS : "perceptors";
MODULES : "modules";
ACTUATORS : "actuators";
BELIEF : "bel";
BELIEVER : "believer";
RULES : "rules";
TRIGGERS : "triggers";
RULE_L: "rl";
RULE_R: "rr";

TRIGGER: "trig";
ARGS : "args";
ARG : "arg";
PACKAGE : "pack" ;
NUMT : "numt";
PLAT : "name";
NUMAGTS : "numagts";
NUMARGS : "numargs";
NTRIG : "numtriggers";
ID : "id";
NACT : "numact";
NPER : "numper";
GUI: "gui";

INITCOMM: "initcomm";
COMMIT: "commit";

DNAME : "namerole";

LPAR options {
  paraphrase = "left parenthesis";
} : '{';

RBR options {
  paraphrase = "right parenthesis";
} :'}'; 

LABEL : "label";
DEPENDS : "depends";
BEL_NUM: "belNum";

AGENTS : "agents";
DESIGNS : "designs";
BELIEFS : "bels";
SERVICES : "services";

SEQUENCES: "sequences";

RES: "resources";

CLASSNAME : "classname";
RTIME : "responsetime";
ROLES : "roles";
COUNTER : "lcount";
NRULES : "numrules";

INTERMOD : "intermod";

FORALL : "forall" ;
AGTNAME : "agtname";
// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
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

WS    : ( ' '
        | '\r' '\n' {newline();}
        | '\n' {newline();}
        | '\t'
        )
        {$setType(Token.SKIP);}
      ;


SL_CODE
	:	"$$" !
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{newline();}
	;

ML_CODE
	:	"$=" !
		(
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='$' }? '='
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('='|'\n'|'\r')
		)*
		"=$" ! 
		
	;