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
 * FIPAHandler.java
 *
 * Created on 22 February 2005, 17:07
 */

package com.agentfactory.mts.mpp_server.fipa;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.ArrayList;
/**
 *
 * @author Conor Muldoon
 */
public class FIPAHandler extends DefaultHandler{
    
    String name;
    ArrayList<String>addresses;
    ArrayList<AgentID>receiver;
    AgentID sender;
    boolean inTo;
    boolean inFrom;
    boolean inName;
    boolean inAddress;
    
    /** Creates a new instance of FIPAHandler */
    public FIPAHandler() {
        receiver=new ArrayList<AgentID>();
        addresses=new ArrayList<String>();
        inTo=false;
        inFrom=false;
        inName=false;
        inAddress=false;
    }
    
    
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {
        
        if (localName.equals("to")){
            inTo=true;
            return;
        }
        if(localName.equals("name")){
            inName=true;
            return;
        }
        if(localName.equals("from")){
            inFrom=true;
            return;
        }if(localName.equals("url")){
            inAddress=true;
        }
        
        
    }
    public void endElement(String namespaceURI, String localName,
            String qualifiedName) throws SAXException {
        
        if(localName.equals("to")){
            inTo=false;
            return;
        }
        if(localName.equals("name")){
            inName=false;
            return;
        }
        if(localName.equals("from")){
            inFrom=false;
            return;
        }
        if(localName.equals("url")){
            inAddress=false;
            return;
        }
        if(localName.equals("agent-identifier")){
            if(inTo){
                receiver.add(new AgentID(name, addresses));
            } else if(inFrom){
                sender=new AgentID(name,addresses);
            }
            addresses=new ArrayList<String>();
        }
        
    }
    
    public void characters(char[] ch, int start, int length)
    throws SAXException {
        String string=new String(ch,start,length).trim();
        if(inName){
            name=string;
            return;
        }
        if(inAddress){
            addresses.add(string);  
        }
    }
    
    
    public void storeMessage(FIPAMessageStore store,String performative,String content){
        
        store.receiveMessage(new IncomingMessage(performative,sender,receiver,content));
    }
    
}
