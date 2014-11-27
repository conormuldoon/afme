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
 * Message.java
 *
 * Created on 18 February 2005, 12:37
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 *
 * @author Conor Muldoon
 */
public class OutgoingMessage {
    
    private static final String aclRepresentation="fipa.acl.rep.string.std";
    private static final String language= "AFAPL";
    private static final String ontology= "None";
    
    private AgentID sender;
    private ArrayList<AgentID>receivers;
    
    
    private int[]payload;
    
    /** Creates a new instance of Message */
    public OutgoingMessage(String performative,AgentID sender,ArrayList<AgentID>receivers,String content) {
        
        this.sender=sender;
        this.receivers=receivers;
        
        
        StringBuilder buf = new StringBuilder();
        buf.append("(").
                append(performative).
                append(" :sender ").
                append(sender.toFIPAString()).
                append(" :receiver (set");
        
        
        for(AgentID aid:receivers)
        {
            buf.append(" ").append(aid.toFIPAString());
        }
        buf.append(")");
        buf.append(" :language ").append(language);
        buf.append(" :ontology ").append(ontology);
        buf.append(" :content ").append(content);
        buf.append(")");
        String text=buf.toString();
        int length=text.length();
        payload=new int[length];
        for (int i = 0; i <length; i++) {
            payload[i]=(int) text.charAt(i);
        }
        
        
        
    }
    
    public void printLogMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append("From: ").append(sender.toString()).append(" To: [");
        boolean first = true;
        int size=receivers.size();
        for (int i = 0; i < size; i++) {
            if (first) {
                first = false;
            } else {
                buf.append(",");
            }
            
            buf.append( receivers.get(i).toString());
        }
        
        buf.append("]");
        
        
    }
    
    public void printEnvelope(PrintWriter out){
        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\"?>\n");
        buf.append("<envelope>\n");
        buf.append("\t<params index=\"1\">\n");
        buf.append("\t\t<to>\n");
        
        
        for(AgentID aid:receivers){
            aid.toXMLString(buf, "\t\t\t");
        }
        
        buf.append("\t\t</to>\n");
        buf.append("\t\t<from>\n");
        sender.toXMLString(buf, "\t\t\t");
        buf.append("\t\t</from>\n");
        buf.append("\t\t<acl-representation>" + aclRepresentation + "</acl-representation>\n");
        buf.append("\t\t<payload-length>" + payload.length+ "</payload-length>\n");
        buf.append("\t\t<intended-receiver>\n");
        
        for (AgentID aid:receivers){
            aid.toXMLString(buf, "\t\t\t");
        }
        buf.append("\t\t</intended-receiver>\n");
        buf.append("\t</params>\n");
        buf.append("</envelope>");
        out.print(buf.toString());
    }
    
    public void printACL(PrintWriter out){
        out.print(aclRepresentation);
    }
    
    public void printPayload(PrintWriter out){

        for (int i = 0; i <payload.length; i++) {
            out.write(payload[i]);
            
        }
    }
    
    public void send()throws java.net.MalformedURLException{
        
        for(AgentID receiver:receivers){
            receiver.send(this);
        }
    }
    
    
}
