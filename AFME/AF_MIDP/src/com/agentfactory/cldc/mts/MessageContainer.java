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
 * MessageContainer.java
 *
 * Created on 24 May 2005, 14:37
 */

package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
/** The message container represents a list of messages.
 *
 * @author Conor Muldoon
 */
public class MessageContainer {
    final static byte SIZE = 8;
    int size;
    Message[]message;
    /** Creates a new instance of MessageContainer. */
    public MessageContainer() {
        message=new Message[SIZE];
    }

    /** Adds a message to the container.
     * 
     * @param msg the message to be added.
     */
    public synchronized void addMessage(Message msg){
        if(size==message.length){
            Message[]array=new Message[message.length<<1];
            System.arraycopy(message,0,array,0,message.length);
            message=array;
        }
        message[size]=msg;
        size++;
    }

    /** Creates a FOS representation of the message list.
     * The FOS takes the form messages(message1,message2,..., messageN).
     * 
     * @return null if there are no messages, otherwise a FOS of the 
     * form messages(message1,message2,...), whereby each message is converted 
     * to its belief string representation.
     * @throws MalformedLogicException if there is a logic error.
     */
    public synchronized FOS createMessageFOS()throws MalformedLogicException{
        if(size==0)return null;
        int i=size-1;

        StringBuffer buffer=new StringBuffer("messages(");
        buffer.append(message[i].toBeliefString());
        while(i-->0){
            buffer.append(",");
            buffer.append(message[i].toBeliefString());
            //System.out.println(((Message)objList.get(i)).toBeliefString());
            message[i]=null;
        }
        buffer.append(")");
        size=0;
        return FOS.createFOS(buffer.toString());
    }

}
