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
 * AID.java
 *
 * Created on 12 April 2005, 11:28
 */

package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;

/** The AID class represents an agent identifier. It contains the name
 * of an agent and its address.
 *
 * @author Conor Muldoon
 */
public class AID {
    FOS name;
    FOS addresses;

    /** Creates a new instance of AID.
     * 
     * @param nm the name of the agent.
     * @param addrs the address of the agent.
     */
    public AID(FOS nm,FOS addrs) {
        this.name=nm;
        this.addresses=addrs;
    }

    /** Checks if the name associated with this ID is equal to the 
     * specified name.
     * 
     * @param fos the specified name.
     * @return true if the agent name equals the specified FOS, false otherwise.
     */
    public boolean nameEquals(FOS fos){
        return name.equals(fos);
    }

    /** Appends the agent ID to a string buffer.
     * 
     * @param buffer the string buffer to append the name to.
     */
    public void appendBeliefString(StringBuffer buffer){
        buffer.append("agentID(");
        name.append(buffer);
        buffer.append(',');
        addresses.append(buffer);
        buffer.append(')');

    }

    /** Adds an outgoing message to the message transport service. The message will
     * be sent to the agent that has this agent ID.
     * 
     * @param mts the message transport service that the outgoing message
     * is to be added to.
     * @param performative the performative of the message.
     * @param agentName the name of the agent sending the message.
     * @param content the content of the message.
     * @return a FOS representation of the message if operating
	 * in asynchronous mode, otherwise null.
     * @throws MalformedLogicException if there is a logic error.
     */
    public FOS addOutgoing(MessageTransportService mts,String performative,AgentName agentName,FOS content)throws MalformedLogicException{
        return mts.addHolder(name,performative,agentName,content,addresses);
    }

    
    public FOS createMessage(String localAddress,AgentName senderName,FOS content){
    	FOS url=addresses.next();
    	addresses.reset();
    	return MessageTransportService.createOutMessage(url.toString(),FOS.createFOS(localAddress),name,senderName,content);
        
    }
}
