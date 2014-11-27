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
 * FIPAMessageStore.java
 *
 * Created on 17 February 2005, 16:26
 */

package com.agentfactory.mts.mpp_server.fipa;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.agentfactory.cldc.mts.ServerConstant;



/**
 *
 * @author Conor Muldoon
 */
public class FIPAMessageStore {
    
    final String FILE_NAME="registeredPlatforms.xml";
    final String PLATFORM="platform";
    final String AGENT="agent";
    //final String ADDRESS="address";
    
    Map<String,ArrayList<IncomingMessage>>map;
    Map<String,String>agentMap;
    int maxID;
    Document doc;
    File file;
    int port;
    
    /** Creates a new instance of FIPAMessageStore */
    public FIPAMessageStore(int prt) {
        port=prt;
        map=new HashMap<String,ArrayList<IncomingMessage>>();
        agentMap=new HashMap<String,String>();
        file=new File(FILE_NAME);
        if(file.exists()){
            InputStream in=null;
            try{
                DOMParser parser = new DOMParser();
                in = new FileInputStream(file);
                InputSource source = new InputSource(in);
                parser.parse(source);
                doc = parser.getDocument();
                NodeList platforms = doc.getElementsByTagName(PLATFORM);
                for(int i=platforms.getLength();i-->0;){
                    
                    
                    NodeList agents=platforms.item(i).getChildNodes();
                    
                    String plat=agents.item(0).getTextContent();
                    maxID=Math.max(maxID,Integer.parseInt(plat));
                    map.put(plat, new ArrayList<IncomingMessage>());
                    
                    for(int j=agents.getLength();j-->1;){
                        Node agent=agents.item(j);
                        ArrayList<String>addresses=new ArrayList<String>();
                        NodeList agentChild=agent.getChildNodes();
                        /*for(int k=agentChild.getLength();k-->1;){
                            addresses.add(agentChild.item(k).getTextContent());
                        }*/
                        // temp hack fix later
                        addresses.add("http://"+InetAddress.getLocalHost().getCanonicalHostName()+":"+port+"/acc");
                        AgentID aid=new AgentID(agentChild.item(0).getTextContent(),addresses);
                        agentMap.put(aid.toString(),plat);
                    }
                }
                
                
            }catch(IOException e){
                e.printStackTrace();
            }catch(org.xml.sax.SAXException e){
                e.printStackTrace();
            }finally{
                try{
                    if(in!=null)in.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            
        }else {
            OutputStream out=null;
            try{
                DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
                doc= impl.createDocument(null, "registered_platforms", null);
                out = new FileOutputStream(file);
                OutputFormat fmt = new OutputFormat(doc);
                XMLSerializer serializer = new XMLSerializer(out, fmt);
                serializer.serialize(doc);
                
                
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(out!=null){
                        out.flush();
                        out.close();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        
        
    }
    
    public synchronized void receiveMessage(IncomingMessage message){
        
        message.addToMap(agentMap,map);
    }
    
    public synchronized void sendMessages(Object identifier,DataOutputStream dos)throws IOException{
        
        
        if(map.containsKey(identifier)){
            ArrayList<IncomingMessage>list=map.get(identifier);
            synchronized(list){
                for(IncomingMessage message:list){
                    message.send(dos);
                }
                dos.writeUTF(ServerConstant.END_MESSAGES);
                list.clear();
            }
        }else{
            dos.writeUTF(ServerConstant.END_MAIL);
            
        }
        
        
    }
    
    public void registerAgent(String identifier,String agentName)throws IOException{
        System.out.println("registering agent: "+agentName);
        NodeList platforms = doc.getElementsByTagName(PLATFORM);
        for(int i=platforms.getLength();i-->0;){
            System.out.println("1");
            Node platform=platforms.item(i);
            NodeList agents=platform.getChildNodes();
            
            String plat=agents.item(0).getTextContent();
            System.out.println("*"+identifier+"="+plat+"*");
            if(plat.equals(identifier)){
                System.out.println("2");
                Element agentElement=doc.createElement(AGENT);
                Text agentText=doc.createTextNode(agentName);
                agentElement.appendChild(agentText);
                platform.appendChild(agentElement);
                
                ArrayList<String>addresses=new ArrayList<String>();
                
                /*InetAddress address=InetAddress.getLocalHost();
                String addr="http://"+address.getCanonicalHostName()+":"+port+"/acc";
                Text addrText=doc.createTextNode(addr);
                Element addrElement=doc.createElement(ADDRESS);
                addrElement.appendChild(addrText);
                agentElement.appendChild(addrElement);
                addresses.add(addr);*/
                addresses.add("http://"+InetAddress.getLocalHost().getCanonicalHostName()+":"+port+"/acc");
                AgentID aid=new AgentID(agentName,addresses);
                agentMap.put(aid.toString(),plat);
                FileOutputStream out = new FileOutputStream(file);
                OutputFormat fmt = new OutputFormat(doc);
                XMLSerializer serializer = new XMLSerializer(out, fmt);
                serializer.serialize(doc);
                break;
            }
            System.out.println("complete "+System.currentTimeMillis());
            
        }
        
        /*
        FileOutputStream out=null;
        synchronized(this){
            Element agentElement=doc.createElement(AGENT);
         
            Element regPlatforms = doc.getDocumentElement();
            doc.getElementsByTagName(identifier);
                /*
                Element platform=doc.createElement(PLATFORM);
                Text text = doc.createTextNode(string);
                platform.appendChild(text);
                regPlatforms.appendChild(platform);
                out = new FileOutputStream(file);
                OutputFormat fmt = new OutputFormat(doc);
                XMLSerializer serializer = new XMLSerializer(out, fmt);
         
         
                for(String agent:agents){
                    Element agentElement=doc.createElement(AGENT);
                    Text agentText=doc.createTextNode(agent);
                    agentElement.appendChild(agentText);
                    platform.appendChild(agentElement);
                    ArrayList<String>addresses=new ArrayList<String>();
                    InetAddress address=InetAddress.getLocalHost();
                    String addr="http://"+address.getCanonicalHostName()+":"+port+"/acc";
                    Text addrText=doc.createTextNode(addr);
                    Element addrElement=doc.createElement(ADDRESS);
                    addrElement.appendChild(addrText);
                    agentElement.appendChild(addrElement);
                    addresses.add(addr);
                    AgentID aid=new AgentID(agent,addresses);
                    agentMap.put(aid.toString(),string);
                }
                serializer.serialize(doc);*/
    }
    
    
    public void registerPlatform(DataOutputStream dos,ArrayList<String>agents)throws IOException{
        FileOutputStream out=null;
        
        try{
            maxID++;
            String string=""+maxID;
            dos.writeUTF(string);
            System.out.println("registering platform "+maxID);
            synchronized(this){
                map.put(string, new ArrayList<IncomingMessage>());
                // use xml
                Element regPlatforms = doc.getDocumentElement();
                Element platform=doc.createElement(PLATFORM);
                Text text = doc.createTextNode(string);
                platform.appendChild(text);
                regPlatforms.appendChild(platform);
                out = new FileOutputStream(file);
                OutputFormat fmt = new OutputFormat(doc);
                XMLSerializer serializer = new XMLSerializer(out, fmt);
                
                
                for(String agent:agents){
                    Element agentElement=doc.createElement(AGENT);
                    Text agentText=doc.createTextNode(agent);
                    System.out.println(maxID+" "+agent);
                    agentElement.appendChild(agentText);
                    platform.appendChild(agentElement);
                    ArrayList<String>addresses=new ArrayList<String>();
                    /*InetAddress address=InetAddress.getLocalHost();
                    String addr="http://"+address.getCanonicalHostName()+":"+port+"/acc";
                    Text addrText=doc.createTextNode(addr);
                    Element addrElement=doc.createElement(ADDRESS);
                    addrElement.appendChild(addrText);
                    agentElement.appendChild(addrElement);
                    addresses.add(addr);*/
                    addresses.add("http://"+InetAddress.getLocalHost().getCanonicalHostName()+":"+port+"/acc");
                    AgentID aid=new AgentID(agent,addresses);
                    agentMap.put(aid.toString(),string);
                }
                serializer.serialize(doc);
            }
            
        }catch(FileNotFoundException e){
            e.printStackTrace();
        } finally{
            out.flush();
            dos.flush();
            out.close();
            
        }
    }
    
}
