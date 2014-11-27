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
 * AgentID.java
 *
 * Created on 18 February 2005, 15:36
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.util.*;
import java.io.DataOutputStream;
/**
 *
 * @author Conor Muldoon
 */
public class AgentID {
    
    String name;
    Collection<String>addresses;
    
    /** Creates a new instance of AgentID */
    public AgentID(String name,Collection<String>addresses) {
        this.name=name;
        this.addresses=addresses;
        
    }
    public void sendName(DataOutputStream dos)throws java.io.IOException{
        dos.writeUTF(name);
    }
    
    public void send(DataOutputStream dos)throws java.io.IOException{
        
        dos.writeUTF(name);
        dos.writeInt(addresses.size());
        for(String address:addresses){
            dos.writeUTF(address);
        }
    }
    
    
    public void toXMLString(StringBuilder buf, String string){
        
        buf.append(string + "<agent-identifier>\n");
        buf.append(string + "\t<name>" + name + "</name>\n");
        buf.append(string + "\t<addresses>\n");
        for(String s:addresses){
            buf.append(string + "\t\t<url>" + s + "</url>\n");
        }
        buf.append(string + "\t</addresses>\n");
        buf.append(string + "</agent-identifier>\n");
        
    }
    
    public String toFIPAString(){
        StringBuffer buf = new StringBuffer();
        buf. append("(agent-identifier ").
                append(":name ").
                append(name).
                append(" :addresses ").
                append("(sequence");
        for(String s:addresses){
            buf.append(" ").append(s);
        }
        
        buf.append("))");
        
        return buf.toString();
    }
    
    public void send(OutgoingMessage message)throws java.net.MalformedURLException{
        for(String address:addresses){
        		
                MessageSender.sendMessage(address,message);
        }
        
    }
    
    public boolean equals(Object o){
        AgentID aid=(AgentID)o;
        return name.equals(aid.name);
    }
    
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        buf.
                append("agentID(").
                append(name).
                append(",addresses(");
        
        Iterator it=addresses.iterator();
        buf.append(it.next());
        while(it.hasNext()){
            buf.append(",");
            buf.append(it.next());
        }
        buf.append("))");
        
        return buf.toString();
    }
    
}
