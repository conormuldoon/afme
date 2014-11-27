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

import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.PerceptionManager;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.scheduler.Scheduler;

/** This is a helper class that is used in cases whereby the developer wishes to construct an agent platform
 * directly in Java and without the use of the AFME compiler.
 * 
 * @author Conor Muldoon
 *
 */
public class Builder {

	Hashtable service;
	Scheduler scheduler;
	
	/** Creates an instance of Builder.
	 * 
	 * @param numThreads the number of threads in the scheduler.
	 */
	public Builder(int numThreads){
		service=new Hashtable();
		scheduler=new Scheduler(numThreads);
		
	}
	
	/**
	 * Creates an instance of Builder with a scheduler containing 3 threads.
	 */
	public Builder(){
		
		service=new Hashtable();
		scheduler=new Scheduler(3);
	}
	
	/** Registers the service with the service table.
	 * 
	 * 
	 * @param serv the service to be registered.
	 */
	public void addService(Service serv){
		serv.register(service);
		
	}
	
	/** Creates an instance of PerceptionManager.
	 * 
	 * @param aw the agent wrapper used to create the manager.
	 * @return a new instance of PerceptionManager.
	 */
	public PerceptionManager createPM(AgentWrapper aw){
		return aw.createPM(service, scheduler);
	}
	
	/** Creates an instance of AffectManager.
	 * 
	 * @param aw the agent wrapper used to create the manager.
	 * @return a new instance of AffectManager.
	 */
	public AffectManager createAM(AgentWrapper aw){
		return aw.createAM(service, scheduler);
	}
	
	/** Schedules a task to be executed using the internal schduler.
	 * 
	 * @param task the task to be executed.
	 */
	public void schedule(Runnable task){
		scheduler.schedule(task);
	}
	
	/** Schedules a task to be executed periodically.
	 * 
	 * @param key an identifier for the task.
	 * @param task the task to be executed.
	 * @param period the period at which the task should be executed.
	 */
	public void schedule(Object key,Runnable task,int period){
		scheduler.schedule(key, task, period);
	}
	
	/** Schedules an agent to be executed periodically.
	 * 
	 * @param aw the wrapper for the agent.
	 * @param responseTime the response time at which the agent should execute.
	 */
	public void scheduleAgent(AgentWrapper aw,int responseTime){
	
		aw.schdule(scheduler, responseTime);
	}
	
	/**
	 * Starts the platform scheduler.
	 */
	public void start(){
		scheduler.start();
	}
	
	/**
	 * Pauses the platform scheduler.
	 */
	public void pause(){
		scheduler.pause();
		
	}
	
	/**
	 * Destroys the platform scheduler.
	 */
	public void destroy(){
		scheduler.destroy();
	}
}
