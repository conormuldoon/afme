/**
 * Copyright:   Copyright (c) 1996-2007 The Agent Factory Working Group. All rights reserved.
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

/**
 *
 * @author Conor Muldoon
 */

package com.agentfactory.cldc.scheduler;
import java.util.Hashtable;

/** The ExTime represents a period at which a task can be executed. All tasks
 * that execute at the same period use the same ExTime object. The ExTime
 * object phase shifts agents so as to reduce computation bottlenecks.
 * 
 * @author Conor Muldoon
 */
public class ExTime {
	int rv;
	int numAg;
	int off;
	int per;
	int i;
	long exeTime;
	int cnt;
	/** Constructs an instance of ExTime.
	 * 
	 * @param period the period at which the tasks should be executed.
	 * @param randVal a random start time value.
	 */
	public ExTime(int period,int randVal){
		per=period;
		numAg=1;
		off=per;
		rv=randVal;
		reSched();
	}

	/** Creates a new execution time value from a combination of
	 * the current time and the random start time.
	 * 
	 *
	 */
	public void reSched(){
		exeTime=System.currentTimeMillis()+rv;
		//System.out.println("Setting initial "+System.currentTimeMillis()+" "+rv);
	}



	long schedule(){
		if(per==-1)return -1;

        long ret=exeTime+(cnt*off);
        cnt++;
        cnt%=numAg;
        if(cnt==0)exeTime+=per;

        //System.out.println(System.currentTimeMillis());


        return ret;
	}

	/** Removes the period from the hashtable if there are no more agents
	 * scheduled to execute at that period.
	 * 
	 * @param table the hashtable the period is to be removed from. 
	 */
	public void removeAgt(Hashtable table){
		numAg--;
		if(numAg==0)table.remove(new Integer(per));
	}
	/** Increase the number of agents operating at a particular period.
	 * This will have an affect on the phase shifting of agents.
	 * 
	 *
	 */
	public void newAgt(){
		numAg++;
		off=per/numAg;
	}

	/** Writes the period at which the agents execute to the data output
	 * stream.
	 * 
	 * @param dos the stream to which the period is written.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	  public void writeTime(java.io.DataOutput dos)throws java.io.IOException{
	    	dos.writeInt(per);
	    }

}
