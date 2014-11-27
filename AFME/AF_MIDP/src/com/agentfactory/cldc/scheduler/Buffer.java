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
 * Buffer.java
 *
 * Created on 09 February 2005, 15:34
 */

package com.agentfactory.cldc.scheduler;

/** The Buffer stores tasks that are to be executed by the worker threads.
 *
 * @author Conor Muldoon
 */
public class Buffer{

    Entry head;
    Entry tail;

    /** Constructs and instance of Buffer.
     * 
     *
     */
    public Buffer(){
        head=null;
        tail=null;
    }

    private class Entry{
        Runnable element;
        Entry next;

        public Entry(Runnable element){
            this.element=element;
            next=null;
        }
    }

    /** Inserts a thread task into the buffer.
     * 
     * @param r the thread task to be inserted.
     */
    public synchronized void insert(Runnable r){

        Entry entry=new Entry(r);
        if(head!=null){
            tail.next=entry;
            tail=entry;
            return;
        }
        head=entry;
        tail=head;
        notify();
    }

    /** Extracts a thread task from the buffer.
     * 
     * @return the next thread task in the buffer.
     */
    public synchronized Runnable extract(){
        try{
            // Spin Lock
            while(head==null)wait();

            Runnable element=head.element;
            head=head.next;
            return element;
        }catch(InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

}


