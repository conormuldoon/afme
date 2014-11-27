header{
package com.agentfactory.cldc.compiler;
import java.io.PrintStream;
import static com.agentfactory.cldc.compiler.Container.*;
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
 
class TemplateWalker extends TreeParser;
options {
    buildAST = true;   // uses CommonAST by default
}

{
	public void print(AST g,PrintStream ps){
		ps.print(g.getText());
	}

}
walk[Container c,PrintStream ps] : ((s:SL_CODE {ps.print(s.getText());})|
		(ml:ML_CODE {ps.print(ml.getText());})|
		(PACKAGE {c.pack(ps);})|
		(NUMT {c.numT(ps);})|
		(PLAT {c.plat(ps);})|
		(NUMAGTS {c.numAgt(ps);})| 
		(forall[c])
		)* ;

forall[Container c]: ((beliefs[c])|(designs[c])|(services[c])|gui[c]|(agents[c])|(starters[c]));

beliefs[Container c]: BELIEFS LPAR (BELIEF{c.addItem(BF);}
|BELIEVER {c.addItem(BLR);}|s:SL_CODE {c.addItem(s.getText());}
|ml:ML_CODE {c.addItem(ml.getText());}|LCNT {c.addItem(LOOP);
})* RBR {c.outBel();};

designs[Container c]: DESIGNS 
LPAR (DNAME {c.addItem(DN);}|(ruleloop[c])
|NRULES {c.addItem(NUMR);}|NACT {c.addItem(NMA);}|NPER {c.addItem(NMP);}
|NMOD {c.addItem(NM);}|(perloop[c])|(actloop[c])|(roleloop[c])|modloop[c]|(sequences[c])|(intermod[c])|s:SL_CODE {c.addItem(s.getText());}
|(initcomm[c])
|ml:ML_CODE {c.addItem(ml.getText());}
|LCNT {c.addItem(LOOP);}
)* RBR {c.outDes();};

sequences[Container c]: SEQUENCES LPAR {c.addItem(SEQ_S);}
 (depends[c]|BEL_NUM {c.addItem(B_NUM);}
 |RULE_L {c.addItem(R_L);}|LABEL {c.addItem(DEP_L);}|ML_CODE{c.addItem(#ML_CODE);}|SL_CODE {c.addItem(#SL_CODE);})* 
 {c.addItem(SEQ_E);}
 RBR;

services[Container c]: SERVICES LPAR (NUMARGS {c.addItem(NARG);}
|(argloop[c])|CLASSNAME{c.addItem(CLS);}|SL_CODE {c.addItem(#SL_CODE);}
|ML_CODE {c.addItem(#ML_CODE);}
|LCNT {c.addItem(LOOP);
})* RBR {c.outServ();};

agents[Container c]: AGENTS LPAR (( RTIME {c.addItem(RT);})|
(RES {c.addItem(RESO);})|
(AGTNAME {c.addItem(ANM);})|(DNAME {c.addItem(DNM);})
|(SL_CODE {c.addItem(#SL_CODE);})|
(ML_CODE {c.addItem(#ML_CODE);})
|(COUNTER {c.addItem(LOOP);}))* RBR {c.outAgt();};

starters[Container c]: STARTERS LPAR (AGTNAME {c.addItem(ANM);}
|SL_CODE {c.addItem(#SL_CODE);}|ML_CODE {c.addItem(#ML_CODE);}|
LCNT {c.addItem(LOOP);})*  RBR {c.outStart();};

roleloop[Container c]: ROLES LPAR {c.addItem(RLS);}
((NRULES {c.addItem(NUMR);}) |(ID {c.addItem(IDT);})|(NTRIG {c.addItem(NTR);})|
ruleloop[c]|triggerloop[c]|
(SL_CODE {c.addItem(#SL_CODE);})|
(ML_CODE {c.addItem(#ML_CODE);})|
(LCNT {c.addItem(LOOP);}))* 
RBR {c.addItem(RLE);};

argloop[Container c]: ARGS {c.addItem(ARS);}
LPAR (ARG{c.addItem(AR);}|SL_CODE{c.addCode(#SL_CODE);}
|ML_CODE{c.addCode(#ML_CODE);}|
COUNTER {c.addItem(LOOP);})* 
RBR {c.addItem(ARE);};



ruleloop[Container c]: RULES  LPAR {c.addItem(RULE_S);}
 (depends[c]|BEL_NUM{c.addItem(B_NUM);}|RULE_L{c.addItem(R_L);}|RULE_R{c.addItem(R_R);}|SL_CODE{c.addCode(#SL_CODE);}|ML_CODE{c.addCode(#ML_CODE);}|COUNTER{c.addItem(LOOP);})* RBR
 {c.addItem(RULEE);}
 ;


 
 triggerloop[Container c]: TRIGGERS {c.addItem(TS);} LPAR 
(TRIGGER {c.addItem(TR);}|SL_CODE {c.addCode(#SL_CODE);}
|ML_CODE {c.addCode(#ML_CODE);}
|COUNTER{c.addItem(LOOP);})* 
RBR {c.addItem(TE);};

initcomm[Container c]: INITCOMM LPAR {c.addItem(COMM_S);}
(SL_CODE{c.addCode(#SL_CODE);}|ML_CODE{c.addCode(#ML_CODE);}|
COMMIT{c.addItem(INI_C);})*  RBR {c.addItem(COMM_E);};

depends[Container c]: DEPENDS LPAR {c.addItem(DEP_S);}
 (LABEL{c.addItem(DEP_L);}
 |SL_CODE{c.addCode(#SL_CODE);}|ML_CODE{c.addCode(#ML_CODE);}
 |COUNTER{c.addItem(LOOP);}
 |BOOL_VAL {c.addItem(BL_V);})* 
 {c.addItem(DEP_E);}
 RBR ;

perloop[Container c]: PERCEPTORS {c.addItem(PS);} cnm[c] {c.addItem(PE);};
actloop[Container c]: ACTUATORS {c.addItem(AS);} cnm[c] {c.addItem(AE);};
modloop[Container c]: MODULES {c.addItem(MS);} cnm[c] {c.addItem(ME);};

intermod[Container c]: INTERMOD {c.addItem(IS);} cnm[c] {c.addItem(IE);};

gui[Container c]: GUI cnm[c] {c.outGUI();};



cnm[Container c]: LPAR
(CLASSNAME {c.addItem(CLS);}|SL_CODE {c.addCode(#SL_CODE);}
 |ML_CODE {c.addCode(#ML_CODE);}|COUNTER {c.addItem(LOOP);})* 
RBR;

