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
 * AffectManager.java
 *
 * Created on 13 January 2005, 11:52
 */

package com.agentfactory.cldc;
import java.util.Hashtable;

import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.logic.RoleLibrary;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The affect manager enables actuators to interact with modules and
 * services.
 *
 * @author Conor Muldoon
 */
public class AffectManager{

    Agent agt;
    Hashtable mod,serv;
    AgentName agtNm;
    Scheduler sch;
    RoleLibrary lib;

    /** Creates a new instance of AffectManager.
     * @param agent the agent that the affect manager belongs to.
     * @param agentName the name of the agent that the affect manager belongs to.
     * @param modules a table of the agent's modules.
     * @param services a table of the services on the platform.
     * @param scheduler the scheduler for the platform.
     * @param library a library of role tempaltes that the agent can adopt.
     */
    public AffectManager(Agent agent,AgentName agentName,Hashtable modules,Hashtable services,Scheduler scheduler,RoleLibrary library) {
        agt=agent;
        mod=modules;
        serv=services;
        agtNm=agentName;
        sch=scheduler;
        lib=library;
    }

    /** Adds a belief to the agent.
     * @param belief the belief to be added.
     */
    public void adoptBelief(FOS belief){
        agt.addActFOS(belief);
    }
    
    /** Updates the agent's resource constraint.
     * 
     * @param fos a FOS representation of the resource
     * constraint.
     */
    public void updateResources(FOS fos){
    	agt.updateResources(fos);
    }

    /** Updates the cost and benefit values of a commitment.
     * 
     * @param benefit the new benefit of the commitment.
     * @param cost the new cost of the commitment.
     * @param id the label of the commitment.
     */
    public void updateValues(FOS benefit,FOS cost,String id){
    	//System.out.println("id: "+id);
    	agt.updateValues(benefit,cost,id);
    }
    /** Causes an agent to create an instance of a role whose identifier
     * matches the specified FOS.
     * @param fos the specified FOS.
     * @throws MalformedLogicException if there is a logic error.
     */
    public void enactRole(FOS fos)throws MalformedLogicException{
        lib.enactRole(fos);
    }

    /** Deactivates the role that has the specified identifier.
     * @param fos the identifier of the role.
     * @throws MalformedLogicException if there is a logic error.
     */
    public void deactivateRole(FOS fos)throws MalformedLogicException{
        lib.deactivateRole(fos);
    }

    /** Removes a belief from the agent.  If the agent does not
	 * contain the specified belief, no error is thrown.
     * @param belief the belief to be removed.
     */
    public void retractBelief(FOS belief){
        agt.retractBelief(belief);
    }

    /** Schedules a task to be executed periodically.
     * 
     * @param key a label to be associated with the task.
     * @param task the task to be executed.
     * @param period the periodic interval at which the task should be executed.
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
    
    /** Puts the agent into a torpor state.
     */
    public void torpor(){
    	sch.torpor(agtNm);
    }
    
    /** Chanages the periodic interval at which the agent executes.
     * @param period the new periodic interval.
     */
    public void reschedule(int period){
        sch.reschedule(agtNm, period);
    }

    /** Performs an action on a module or service. The object or service
     * are identified by the specified object key. The action ID is used to 
     * identify which action to perform on the module or service in question. 
     * The action ID is required because a module or service may perform several
     * actions. The data FOS is passed to the processAction
     * method of the module or service in question. Modules and services may
     * return information to the calling actuator in the form of a FOS.
     * 
     * @param key the name of the module or service.
     * @param actionID the ID of the action to be performed.
     * @param data that data that is to be used in performing the action.
     * @return a FOS that represents information coming the module
     * or service, otherwise, if no information, null.
     */
    public FOS actOn(Object key,int actionID,FOS data){
        try{
            if(serv.containsKey(key)){
                Service service=(Service)serv.get(key);
                return service.processAction(agtNm,actionID,data);

            }
            Module module=(Module)mod.get(key);
            if(module==null){
            	System.out.println("No Service or Module associated with "+key);
            	return null;
            }
            else return module.processAction(actionID,data);
        }catch(MalformedLogicException e){
            e.printStackTrace();
            return null;
        }
    }
    
    


}
