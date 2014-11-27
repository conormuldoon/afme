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
 * WorkerThread.java
 *
 * Created on 09 February 2005, 15:34
 */

package com.agentfactory.cldc.scheduler;
import java.util.Vector;
/** The worker threads extract tasks from the task buffer and execute them.
 *
 * @author Conor Muldoon
 */
public class WorkerThread extends Thread{
    private Buffer buffer;
    private boolean alive;
    private Vector activeSet;

    /** Creates a new instance of WorkerThread.
     * 
     * @param buffer the buffer to extract tasks from.
     * @param activeSet the set of active tasks.
     */
    public WorkerThread(Buffer buffer,Vector activeSet) {
        this.buffer=buffer;
        this.activeSet=activeSet;
        alive=true;
    }

    /** While the thread is alive, removes tasks from the task buffer, executes them, and
     * then removes them from the active set.
     * 
     */
    public void run(){
        while(alive){
            Runnable runnable=buffer.extract();

            runnable.run();

            activeSet.removeElement(runnable);


        }
    }
    /** Toggles the worker thread from active to inactive and vice versa.
     * 
     *
     */
    public void toggle(){
        alive=(alive)?false:true;
    }

    /** Kills the thread (Provided the buffer has been interrupted).
     * 
     *
     */
    public void destroy(){
        alive=false;
    }


}
