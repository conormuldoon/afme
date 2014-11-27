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

/*
 * PerceptorManager.java
 *
 * Created on 07 January 2005, 14:07
 */

package com.agentfactory.cldc;
import java.util.Hashtable;

import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The perception manager enables perceptors to interact with modules
 * and services.
 *
 * @author Conor Muldoon
 */
public class PerceptionManager{
    Agent agent;
    Hashtable modules,services;
    AgentName agentName;
    Scheduler sch;
    /** Creates a new instance of PerceptorManager 
     * @param agent the agent that the perception manager belongs to.
     * @param agentName the name of the agent that the perception manager belongs to.
     * @param modules the modules for the agent.
     * @param services the services for the platform.
     * @param sched the scheduler for the platform.
     */
    public PerceptionManager(Agent agent,AgentName agentName,Hashtable modules,Hashtable services,Scheduler sched) {
        this.agent=agent;
        this.modules=modules;
        this.services=services;
        this.agentName=agentName;
        sch=sched;
    }

    /** Associates a task with a key and schedules it to be executed 
     * at a specific periodic interval.
     * @param key the key the task is to be associated with.
     * @param task the task to be executed.
     * @param period the period at which the task should be executed.
     */
    public void scheduleTask(Object key,Runnable task,int period){
        sch.schedule(key,task, period);
    }

    /** Schedules a task to be executed in a once off manner.
     * @param task the task to be executed.
     */
    public void scheduleTask(Runnable task){
        sch.schedule(task);
    }
    /*
    public void adoptBelief(String belief){
        try{
            agent.adoptBelief(belief);
        }catch(MalformedLogicException e){
            e.printStackTrace();
        }
    }*/

    /** Adds a belief to the agent.
     * @param fos the belief to be added.
     */
    public void adoptBelief(FOS fos){
        agent.addFOSBelief(fos);
    }

    /** Returns a FOS that represents the information that has
     * been perceived in a module or a service. The module or service is identified
     * through the use of the object key. The perception ID is used to determine
     * between different types of perception that can be performed on the module
     * or service in question. If no module or service match the key specified, null
     * will be returned. If no information has been perceived, null will be returned.
     * 
     * @param key the key of the module or service that the agent wishes to perceive.
     * @param perceptionID the ID of the information within the module or service that the agent 
     * wishes to perceive.
     * @return a FOS that represents the information that has
     * been perceived in a module or a service. 
     */
    public FOS perManage(Object key,int perceptionID){
        try{
            if(services.containsKey(key)){
                Service service=(Service)services.get(key);
                return service.processPer(agentName,perceptionID);

            }
            Module module=(Module)modules.get(key);
            if(module==null){
            	System.out.println("No Service or Module associated with "+key);
            	return null;
            }
            return module.processPer(perceptionID);
        }catch(MalformedLogicException e){
            e.printStackTrace();
            return null;
        }
    }

    /** Appends the agents name to a string buffer.
     * @param buffer the buffer the name is to be appended to.
     */
    public void appendName(StringBuffer buffer){
        agentName.appendName(buffer);
    }

    /** Wakes an agent up from a torpor state.
     */
    public void wake(){
    	sch.wake(agentName);
    }

}
