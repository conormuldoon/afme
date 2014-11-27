/**
 * Copyright:   Copyright (c) 1996-2009 The Agent Factory Working Group. All rights reserved.
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

package com.agentfactory.cldc.builder;

import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.RoleLibrary;
import com.agentfactory.cldc.logic.RoleTemplate;
import com.agentfactory.cldc.logic.TerImplication;

/** The class is used to aid the developer in the construction (in cases where the AFME compiler is not being used) of roles.
 * 
 * @author Conor Muldoon
 *
 */
public class Role {

	public static final int SIZE=8;
	FOS identifier;
	FOS[]trig;
	int curT;
	TerImplication[]terImp;
	int curI;
	
	/** Creates an instance of Role.
	 * 
	 * @param id the identifier for the role.
	 */
	public Role(String id){
		identifier=FOS.createFOS(id);
		terImp=new TerImplication[SIZE];
		trig=new FOS[SIZE];
	}
	
	/** Adds a trigger to the role.
	 * 
	 * @param trigger a string representation of the trigger to be added.
	 */
	public void addTrigger(String trigger){
		if(curT==trig.length){
			FOS[]newArr=new FOS[trig.length<<1];
			System.arraycopy(trig,0,newArr,0,trig.length);
			trig=newArr;
		}
		trig[curT++]=FOS.createFOS(trigger);
	}
	
	/** Adds a rule to the role.
	 * 
	 * @param rule a string representation of the rule to be added.
	 */
	public void addRule(String rule){
		if(curI==terImp.length){
			TerImplication[]newArr=new TerImplication[terImp.length<<1];
			System.arraycopy(terImp,0,newArr,0,terImp.length);
			terImp=newArr;
		}
		terImp[curI++]=AgentWrapper.createRule(rule);
	}
	
	/** Add the role to the specified role library.
	 * 
	 * @param lib the role library the role is to be added to.
	 */
	public void addToLibrary(RoleLibrary lib){
		if(terImp.length>curI){
			TerImplication []ti=new TerImplication[curI];
			System.arraycopy(terImp, 0, ti, 0, curI);
			terImp=ti;
		}
		if(trig.length>curT){
			FOS t[]=new FOS[curT];
			System.arraycopy(trig, 0, t, 0, curT);
			trig=t;
		}
		lib.add(identifier, new RoleTemplate(terImp,identifier,trig));
	}
}
