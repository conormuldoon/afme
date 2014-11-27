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
 * ThreadTask.java
 *
 * Created on 09 February 2005, 16:14
 */

package com.agentfactory.cldc.scheduler;

import java.util.Hashtable;

/** In AFME, agents and other tasks are scheduled to execute at periodic intervals.
 * The task to be scheduled must implement the runnable interface. The ThreadTask
 * associates the task to be executed with an execution time. The scheduler places
 * thread task in a buffer. At execution time the thread task is extracted from the
 * buffer and subsequently executed by worker threads.
 *
 * @author Conor Muldoon
 */
public class ThreadTask implements Runnable{

    Runnable runnable;
    
    ExTime time;
    long exeTime;
    
    /** Creates a new instance of ThreadTask.
     * 
     * @param runnable the task to be executed.
     * @param et the execution time.
     */
    public ThreadTask(Runnable runnable,ExTime et) {

        
        time=et;
        this.runnable=runnable;
        schedule();
        

    }

    /** If there are no more agents schedule at the execution time interval,
     * the execution time interval will be removed from the specified hash table.
     * 
     * @param table the hash table that the execution time interval is to be removed from.
     */
    public void removeTime(Hashtable table){
    	time.removeAgt(table);
    }
    
    /** Checks whether the runnable task within this thread task is equal to
     * the specified runnable task.
     * 
     * @param run the specified runnable task.
     * @return true if this runnable task is equal to the specified task, false otherwise.
     */
    public boolean equalsProcess(Runnable run){
        return run==runnable;
    }


    /** Checks whether this task will execute sooner than the specified task.
     * 
     * @param task the specified task.
     * @return true if the execution time is sooner, false otherwise.
     */
    public boolean sooner(ThreadTask task){
        return exeTime<task.exeTime;
    }

    /** Calculates the amount of time the scheduler should sleep until it 
     * reaches the execution time.
     * 
     * @return the amount of time the scheduler should sleep.
     */
    public long calcSleepTime(){
        return exeTime-System.currentTimeMillis();
    }
    int a=1;
    
    /** Calculates the next execution time at which the task will be executed.
     * 
     * @return false if the task is to be executed now, true otherwise.
     * @throws com.agentfactory.cldc.logic.MalformedLogicException if there is a logic error.
     */
    public boolean schedule()throws com.agentfactory.cldc.logic.MalformedLogicException{
    	
	    exeTime=time.schedule();
	    
	    if(exeTime==-1){
	    	exeTime=System.currentTimeMillis();
	    	return false;
	    }
	    return true;
    }

    /** Executes the runnable task.
     */
    public void run(){
        runnable.run();
    }
    
    /** Writes the state of the runnable object (Agent) to the specified data output
     * stream.
     * 
     * @param dos the data output stream to write the state of the agent to.
     * @throws java.io.IOException if there is an I/O error.
     */
    public void writeAgent(java.io.DataOutput dos)throws java.io.IOException{
    	((AgentRunnable)runnable).writeToStream(dos);
    }
    
    /** Writes the execution time to the specified data output stream.
     * 
     * @param dos the data output stream to write the execution time to.
     * @throws java.io.IOException if there is an I/O error.
     */
    public void writeTime(java.io.DataOutput dos)throws java.io.IOException{
    	time.writeTime(dos);
    }
    
    
}
