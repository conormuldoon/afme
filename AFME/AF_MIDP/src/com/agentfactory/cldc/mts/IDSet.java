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
 * IDSet.java
 *
 * Created on 24 May 2005, 14:56
 */

package com.agentfactory.cldc.mts;

import java.util.Hashtable;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
/** A set of agent IDs.
 *
 * @author Conor Muldoon
 */
public class IDSet {
    AID[]aid;
    final static byte SIZE=8;
    int size;

    /** Creates a new instance of IDSet. */
    public IDSet() {
        aid=new AID[SIZE];
    }

    /** Adds an agent ID to the set.
     * 
     * @param id the agent ID that is added.
     */
    public void addID(AID id){
        if(size==aid.length){
            AID[]array=new AID[aid.length<<1];
            System.arraycopy(aid,0,array,0,size);
            aid=array;
        }
        aid[size]=id;
        size++;
    }

    /** This method adds a message to the outgoing list if the agent ID of the 
     * agent that the message is intended for is in the set. Otherwise the message
     * is stored in the pending hashtable until the relevant agent ID is obtained.
     * 
     * @param mts the local message transport service.
     * @param messageTo the name of the agent that the message is to be sent to.
     * @param performative the performative of the message.
     * @param agentName the name of the message sender.
     * @param content the content of the message.
     * @param pending a hashtable that stores pending messages.
     * @return null if the ID of the agent that the message is for is not in the set or if the
     * message transport service is operating in synchronous mode. Otherwise a FOS
     * representation of the message is returned.
     * @throws MalformedLogicException if there is a logic error.
     */
    public FOS sendOut(MessageTransportService mts,FOS messageTo,String performative,AgentName agentName, FOS content,Hashtable pending)
    throws MalformedLogicException{
        for(int i=size;i-->0;)if(aid[i].nameEquals(messageTo)){
            return aid[i].addOutgoing(mts, performative, agentName, content);
        }

        addPending(messageTo, performative, agentName,content,pending);
        
        return null;

    }
    
    private void addPending(FOS messageTo,String performative,AgentName agentName, FOS content,Hashtable pending){
    	 PendingMessages pendMess=(PendingMessages)pending.get(agentName);
         pendMess.addPending(messageTo,performative, content);
    }

    /** If the agent that the message is intended for is on the local platform,
     * the message is created and returned, otherwise, it is added to the pending
     * hashtable.
     * 
     * @param localAddress the local address of the platform.
     * @param messageTo the agent that the message is intended for.
     * @param performative the performative of the message.
     * @param senderName the name of the agent that sent the message.
     * @param content the content of the message.
     * @param pending the hashtable of pending messages.
     * @return the created message if the recipient is on the local platform, null otherwise.
     */
    public FOS createMessage(String localAddress,FOS messageTo,String performative,AgentName senderName, FOS content,Hashtable pending){
    	for(int i=size;i-->0;)if(aid[i].nameEquals(messageTo)){
            return aid[i].createMessage(localAddress,senderName,content);
        }
    	
    	addPending(messageTo, performative, senderName,content,pending);
        
        return null;
    }
    
    /** Creates a FOS representation of the ID set.
     * The FOS will be of the form id(?id1,?id2,...?idN).
     * 
     * @return the FOS representation of the ID set.
     * @throws MalformedLogicException if there is a logic error.
     */
    public FOS createIDFOS()throws MalformedLogicException{
        if(size==0)return null;
        StringBuffer buffer=new StringBuffer("id(");
        int i=size-1;
        aid[i].appendBeliefString(buffer);
        while(i-->0){
            buffer.append(',');
            aid[i].appendBeliefString(buffer);
        }
        buffer.append(')');
        return FOS.createFOS(buffer.toString());
    }

}
