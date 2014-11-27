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

import java.util.Hashtable;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.BasicRunnable;
import com.agentfactory.cldc.Module;
import com.agentfactory.cldc.PerceptionManager;
import com.agentfactory.cldc.Perceptor;
import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.logic.BelSeq;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.RoleLibrary;
import com.agentfactory.cldc.logic.TerImplication;
import com.agentfactory.cldc.scheduler.Scheduler;

/** This class is used to aid the developer in the construction of
 * agents in cases whereby they are not using the AFME compiler, which
 * automates the procedure.
 * 
 * @author Conor Muldoon
 *
 */
public class AgentWrapper {

	Agent agent;
	AgentName an;
	Hashtable module;
	int curPer,curTer;
	Hashtable actuator;
	Perceptor[]per;
	RoleLibrary lib;
	TerImplication []terImp;
	
	/** Creates a new instance of AgentWrapper. Note the value used for the amount of
	 * resources available to the agent is irrelevant if there are no
	 * utility values specified in the commitment rules.
	 * 
	 * @param agentName the name of the agent.
	 * @param numPerceptor the number of perceptors the agent has.
	 * @param numRule the number of commitment rules the agent has.
	 * @param resource the amount of resources the agent has.
	 */
	public AgentWrapper(AgentName agentName,int numPerceptor,int numRule,int resource){
		an=agentName;
		init(numPerceptor,numRule,resource);
	}
	
	private void init(int numPerceptor,int numRule,int resource){
		per=new Perceptor[numPerceptor];
		actuator=new Hashtable();
		module=new Hashtable();
		lib=new RoleLibrary();	
		terImp=new TerImplication[numRule];
		agent=new Agent(terImp,actuator,per,module,lib,resource);
	}
	
	/** Creates a new instance of AgentWrapper. Note the value used for the amount of
	 * resources available to the agent is irrelevant if there are no
	 * utility values specified in the commitment rules.
	 * 
	 * @param name the name of the agent.
	 * @param app the application name.
	 * @param numPerceptor the number of perceptors the agent has.
	 * @param numRule the number of commitment rules the agent has.
	 * @param resource the amount of resources the agent has.
	 */
	public AgentWrapper(String name,String app,int numPerceptor,int numRule,int resource){
		an=new AgentName(name,app);
		init(numPerceptor,numRule,resource);
	}
	
	/** Creates a new instance of AffectManager.
	 * 
	 * @param service a table of platform services.
	 * @param scheduler the platform scheduler.
	 * @return an affect manager for the agent.
	 */
	public AffectManager createAM(Hashtable service,Scheduler scheduler){
		return new AffectManager(agent,an,module,service,scheduler,lib);
	}
	
	/** Creates a new instance of PerceptionManager.
	 * 
	 * @param service a table of platform services.
	 * @param scheduler the platform scheduler.
	 * @return a perception manager for the agent.
	 */
	public PerceptionManager createPM(Hashtable service, Scheduler scheduler){
		return new PerceptionManager(agent,an,module,service,scheduler);
	}
	
	/** Adds a module to the wrapper.
	 * 
	 * @param m the module to be added.
	 */
	public void addModule(Module m){
		m.register(module);
		
	}
	
	/** Adds an initial belief to the agent.
	 * 
	 * @param s a string representation of the belief.
	 */
	public void addInitialBelief(String s){
		agent.addFOSBelief(FOS.createFOS(s));
	}
	
	/** Adds a perceptor to the wrapper.
	 * 
	 * @param p the perceptor to be added.
	 */
	public void addPerceptor(Perceptor p){
		per[curPer++]=p;
	}
	
	/** Adds an actuator to the wrapper.
	 * 
	 * @param a the actuator to be added.
	 */
	public void addActuator(Actuator a){
		a.register(actuator);
		
	}
	
	/** Adds a role to the wrapper.
	 * 
	 * @param r the role to be added.
	 */
	public void addRole(Role r){
		r.addToLibrary(lib);
	}
	
	/**
	 * Parses the specified rule and creates an instance of TerImplication. It should be noted
	 * that when creating rules in this manner support is not currently
	 * provided for belief labels or mathematical expressions. If the developer wishes to use
	 * belief labels or mathematical expressions, they must use the AFME compiler for creating the
	 * platform.
	 * 
	 * @param rule a string representation of the commitment rule.
	 * @return the rule that has been created.
	 */
	public static TerImplication createRule(String rule){
		StringBuffer sb=new StringBuffer();
		int n=rule.length();
		for(int i=0;i<n;i++){
			char c=rule.charAt(i);
			if(c=='\n'||c==' '||c=='\r'||c=='\t')continue;
			sb.append(rule.charAt(i));
		}
		// do some parsing here...
		String rl=sb.toString();
		int ind=rl.lastIndexOf('>');
		
		String ss=rl.substring(0,ind);
		FOS fos=FOS.createFOS("bels("+ss+')');
		int ind2=rl.lastIndexOf('\'');
		if(ind2<0)ind2=rl.length();
		FOS action=FOS.createFOS("act("+rl.substring(ind+1,ind2)+')');
		StringBuffer belSeq=new StringBuffer(fos.next().toString());
		int bcnt=1;
		for(FOS f=fos.next();f!=null;f=fos.next()){
			belSeq.append(',');
			belSeq.append(f.toString());
			bcnt++;
		}
		
		int acnt=1;
		
		StringBuffer aBuffer=new StringBuffer(action.next().toString());
		
		for(FOS f=action.next();f!=null;f=action.next()){
			aBuffer.append(',');
			aBuffer.append(f.toString());
			acnt++;
		}
		
		BelSeq[]arr=new BelSeq[0];
        boolean[]bool=new boolean[0];
        return new TerImplication(aBuffer.toString()+rl.substring(ind2)+acnt,new BelSeq(belSeq.toString()+'|'+bcnt,arr,bool));
	
	}
	
	/** Adds a rule to the wrapper.
	 * 
	 * @param rule a string representation of the rule to be added.
	 */
	public void addRule(String rule){
		
		terImp[curTer++]=createRule(rule);
	}
	
	/**
	 * Schedules the agent to be executed at the specified response time.
	 *
	 * @param scheduler the scheduler for the platform.
	 * @param responseTime the response time for the agent.
	 */
	public void schdule(Scheduler scheduler,int responseTime){
		
		scheduler.schedule(an,new BasicRunnable(agent), responseTime);
	}
	
	
}
