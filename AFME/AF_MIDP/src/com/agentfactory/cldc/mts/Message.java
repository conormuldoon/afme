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

 package com.agentfactory.cldc.mts;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;

/** This class represents an AFME message. AFME messages comprise a performative,
 * a message sender, the sender's address, and the information content of the
 * message.
 * 
 * @author Conor Muldoon
 */
public class Message {
    // Performatives

    public static final String ACTION                           = "action";
    public static final String INFORM                           = "inform";
    public static final String REQUEST                          = "request";

    private String performative;
    private AgentName senderName;
    private Object[]senderAddress;
    private FOS content;

    
    
	/** Creates an instance of Message with no sender addresses.
	 * @param performative the performative of the message.
	 * @param senderName the name of the agent that sent the message.
	 * @param content the information content of the message.
	 */
    public Message(String performative,AgentName senderName,FOS content){
        this.performative=performative;
        this.senderName=senderName;
        this.senderAddress=null;
        this.content=content;
    }
    
    //  Fix this for multiple Addresses;
    
    /** Creates an instance of Message with sender addresses.
     * 
     * @param performative the performative of the message.
     * @param senderName the name of the agent that sent the message.
     * @param sendAddr the address of the sender agent.
     * @param content the information content of the message.
     */
    public Message(String performative,AgentName senderName,Object sendAddr,FOS content){
        this.performative=performative;
        this.senderName=senderName;
        this.senderAddress=new Object[1];
        senderAddress[0]=sendAddr;
        this.content=content;
    }

    /** Creates a message from a data input stream.
     * 
     * @param dis the data input stream that the message is be constructed from.
     * @param app
     * @param p the local platform, upon which the agents reside.
     * @throws IOException if there is an I/O error.
     */
    public Message(DataInputStream dis,String app,Platform p)throws IOException{
        performative=dis.readUTF().toLowerCase();

        String string=dis.readUTF();

        try{
            senderName=new AgentName(FOS.createFOS(string),app,p);
            int numAddress=dis.readInt();
            senderAddress=new Object[numAddress];
            for(int i=0;i<numAddress;i++)senderAddress[i]=dis.readUTF();
            content=FOS.createFOS(dis.readUTF());

        }catch(MalformedLogicException e){
            e.printStackTrace();
        }
    }

    /** Converts the message to a belief string.
     * 
     * @return the string representation of the message.
     */
    public String toBeliefString(){

        StringBuffer buf = new StringBuffer();
        buf.
                append("message(").
                append(performative).
                append(",sender(");
        senderName.appendName(buf);
        buf.append(",");

        int size=senderAddress.length;
        buf.append("addresses(");
        buf.append(senderAddress[0]);
        for(int i=1;i<size;i++){
            buf.append(",");
            buf.append(senderAddress[i]);
        }
        buf.append(')').
                append("),").
                append(content).
                append(')');
        return buf.toString();

    }

    /** Writes the state of the message to a data output stream.
     * 
     * @param dos the data output stream that the state is to be
     * written to.
     * @throws IOException if there is an I/O error.
     */
    public void write(DataOutputStream dos)throws IOException{


        dos.writeUTF(performative);
        senderName.write(dos);

        /*
        int size=senderAddress.size();
        dos.writeInt(size);
        for(int i=0;i<size;i++){
            dos.writeUTF((String)senderAddress.get(i));
        }*/
        dos.writeUTF(content.toString());

        //System.out.println("sending: "+performative+" "+senderName+" "+content);
    }


}
