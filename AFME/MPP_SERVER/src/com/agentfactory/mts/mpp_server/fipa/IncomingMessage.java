// Copyright:   Copyright (c) 1996-2006 The Agent Factory Working Group. All rights reserved.
// Licence:     This file is free software; you can redistribute it and/or modify
//              it under the terms of the GNU Lesser General Public License as published by
//              the Free Software Foundation; either version 2.1, or (at your option)
//              any later version.
//
//              This file is distributed in the hope that it will be useful,
//              but WITHOUT ANY WARRANTY; without even the implied warranty of
//              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//              GNU Lesser General Public License for more details.
//
//              You should have received a copy of the GNU Lesser General Public License
//              along with Agent Factory Micro Edition; see the file COPYING.  If not, write to
//              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//              Boston, MA 02111-1307, USA.

/*
 * IncomingMessage.java
 *
 * Created on 18 February 2005, 18:44
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.util.ArrayList;
import java.util.*;
import java.io.DataOutputStream;
/**
 *
 * @author Conor Muldoon
 */
public class IncomingMessage {
    
    String performative;
    AgentID sender;
    ArrayList<AgentID>receivers;
    String content;
    
    /** Creates a new instance of IncomingMessage */
    public IncomingMessage(String performative,AgentID sender,ArrayList<AgentID>receivers,String content) {
        this.performative=performative;
        this.sender=sender;
        this.receivers=receivers;
        this.content=content;
        System.out.println("incoming message: "+performative+' '+content);
        for(AgentID aid: receivers){
        	System.out.println("To: "+aid);
        }
    }
    public void addToMap(Map<String,String>agentMap,Map<String,ArrayList<IncomingMessage>>map){
        
        for(AgentID aid:receivers){
            
            String identifier=agentMap.get(aid.toString());
            System.out.println("storing message in: "+aid.toString());
            if(map.containsKey(identifier)){
                ArrayList<IncomingMessage>list=map.get(identifier);
                synchronized(list){
                    list.add(this);
                }
            }else System.err.println(aid.toString()+" not registered "+System.currentTimeMillis());
        }
    }
    
    public void send(DataOutputStream dos)throws java.io.IOException{
        for(AgentID receiver:receivers){
            receiver.sendName(dos);
            dos.writeUTF(performative);
            sender.send(dos);      
        }
        dos.writeUTF(content);
    }
    
}
