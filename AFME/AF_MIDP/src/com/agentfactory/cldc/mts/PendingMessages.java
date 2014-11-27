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
 * PendingMessages.java
 *
 * Created on 24 May 2005, 15:17
 */

package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.scheduler.Scheduler;
/** This class stores messages that an agent wishes to send to another agent (or
 * agents)
 * but cannot since it does not have the other agent's address. Once the agent
 * receives the address of the other agent, the messages pending are sent.
 *
 * @author Conor Muldoon
 */
public class PendingMessages {

    final static byte SIZE=16;
    int size;
    Pending[]pending;
    FOS[]required;
    int reqSize;

    class Pending{
        String performative;
        FOS messageTo,content;

        public Pending(FOS messTo,String perf,FOS cont){
            messageTo=messTo;
            performative=perf;
            content=cont;
        }
    }

    /** Creates a new instance of PendingMessages. */
    public PendingMessages() {
        pending=new Pending[SIZE];
        required=new FOS[SIZE];
    }

    
    /** Creates a FOS representation of the names
     * of the agents whose agent IDs are required. The FOS
     * takes the form req(requiredName1,requiredName2,...,requiredNameN).
     * 
     * 
     * @return null if no agent IDs are required, otherwise a FOS
     * representation of the agent names.
     * @throws MalformedLogicException if there is a logic error.
     */
    public synchronized FOS contstructPending()throws MalformedLogicException{
        if(reqSize==0)return null;
        int i=reqSize-1;
        StringBuffer buffer=new StringBuffer("req(");
        appendID(buffer,required[i]);
        while(i-->0){
            buffer.append(",");
            appendID(buffer,required[i]);
        }
        buffer.append(")");
        return FOS.createFOS(buffer.toString());
    }
    void appendID(StringBuffer buffer,FOS id){
        buffer.append("AIDRequired(");
        id.append(buffer);
        buffer.append(")");
    }

    /** Once an agent adds an agent ID, this method is called to check
     * if there are any pending messages for the agent ID from the agent
     * that added the ID. If there are pending messages, they are removed
     * from the pending list and added to the outgoing messages or sent
     * on appropriately.
     * 
     * @param mts the local message transport service.
     * @param fos the name of the agent whose agent ID has been added.
     * @param aid the added agent ID.
     * @param agentName the name of the agent that added the agent ID.
     * @throws MalformedLogicException if there is a logic error.
     */
    public synchronized void sendOut(MessageTransportService mts,FOS fos,AID aid,AgentName agentName)
    {

        for(int i=size;i-->0;){
            Pending pm=pending[i];
            if(fos.functorEquals(pm.messageTo)){
                aid.addOutgoing(mts, pm.performative,agentName,pm.content);
                int numMoved=size-i-1;
                if(numMoved>0)System.arraycopy(pending, i+1, pending,i,numMoved);
                pending[--size]=null;
            }
        }
        removeRequired(fos);

    }
    private void removeRequired(FOS fos){
    	 for(int i=reqSize;i-->0;)if(fos.functorEquals(required[i])){
             int numMoved=reqSize-i-1;
             if(numMoved>0)System.arraycopy(required, i+1, required,i,numMoved);
             required[--reqSize]=null;
             return;
         }
    }
    /** Schedules pending messages to be sent.
     * 
     * @param sched the scheduler for the platform.
     * @param localAddress the local address of the platform.
     * @param fos the FOS to be removed from the required list.
     * @param aid the id of the agent that the message is being sent to.
     * @param senderName the name of the agent sending the message.
     */
    public synchronized void sendMessages(Scheduler sched,String localAddress,FOS fos,AID aid,AgentName senderName){
    	  for(int i=size;i-->0;){
              Pending pm=pending[i];
              if(fos.functorEquals(pm.messageTo)){
                  final FOS f=aid.createMessage(localAddress,senderName,pm.content);
                  final String perf=pm.performative;
                  sched.schedule(new Runnable(){
                	  public void run(){
                		  Sender.send(f,perf);
                	  }
                  });
                  int numMoved=size-i-1;
                  if(numMoved>0)System.arraycopy(pending, i+1, pending,i,numMoved);
                  pending[--size]=null;
              }
          }
    	removeRequired(fos);
    }
    

    /** Adds a pending message to the pending messages list.
     * 
     * @param messageTo the agent the message is for.
     * @param performative the performative of the message.
     * @param content the information content of the message.
     */
    public synchronized void addPending(FOS messageTo,String performative,FOS content){
        if(size==pending.length){
            Pending[]array=new Pending[size<<1];
            System.arraycopy(pending,0,array,0,size);
            pending=array;
        }
        pending[size]=new Pending(messageTo,performative,content);
        size++;
        for(int i=reqSize;i-->0;)if(required[i].equals(messageTo))return;
        if(reqSize==required.length){
            FOS[]array=new FOS[reqSize<<1];
            System.arraycopy(required,0,array,0,reqSize);
            required=array;
        }
        required[reqSize]=messageTo;
        reqSize++;

    }

}
