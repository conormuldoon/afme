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
 * ThreadWorkers.java
 *
 * Created on 08 February 2005, 18:47
 */

package com.agentfactory.mts.mpp_server;
import static com.agentfactory.cldc.mts.ServerConstant.CHECK_MAIL;
import static com.agentfactory.cldc.mts.ServerConstant.DIRECT_MESSAGE;
import static com.agentfactory.cldc.mts.ServerConstant.END_MAIL;
import static com.agentfactory.cldc.mts.ServerConstant.END_REGISTER;
import static com.agentfactory.cldc.mts.ServerConstant.REGISTER;
import static com.agentfactory.cldc.mts.ServerConstant.REGISTER_AGENT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.mts.mpp_server.fipa.AgentID;
import com.agentfactory.mts.mpp_server.fipa.FIPAMessageStore;
import com.agentfactory.mts.mpp_server.fipa.IncomingMessage;
import com.agentfactory.mts.mpp_server.fipa.OutgoingMessage;
/**
 *
 * @author Conor Muldoon
 */
public class MessageWorker extends Thread{
    
    SocketBuffer buffer;
    FIPAMessageStore store;
    int fipaPort;
    
    /** Creates a new instance of ThreadWorkers */
    public MessageWorker(SocketBuffer buffer,FIPAMessageStore store,int fipaPort) {
        
        this.buffer=buffer;
        this.store=store;
        this.fipaPort=fipaPort;
    }
    
    public void receiveMessages(DataInputStream dis,DataOutputStream dos)throws IOException{
        String line;
        while(!(line=dis.readUTF()).equals(END_MAIL)){
            
            ArrayList<String>toAddresses=new ArrayList<String>();
            
            int addressSize=dis.readInt();
            
            for(int i=0;i<addressSize;i++){
                toAddresses.add(dis.readUTF());
            }
            receive(dis,dos,line,toAddresses);
            
        }
    }
    void receive(DataInputStream dis,DataOutputStream dos,String line,ArrayList<String>toAddr)throws IOException{
        String performative=dis.readUTF();
        String senderName=dis.readUTF();
        
        ArrayList<String>addresses=new ArrayList<String>();
        InetAddress inet=InetAddress.getLocalHost();
        addresses.add("http://"+inet.getCanonicalHostName()+":"+fipaPort+"/acc");
        AgentID sender=new AgentID(senderName,addresses);
        
        /*
            for(int i=0;i<addSize;i++){
                addresses.add(dis.readUTF());
            }*/
        String content=dis.readUTF();
        System.out.println("receiving: "+performative+" "+senderName+" "+content);
        ArrayList<AgentID>to=new ArrayList<AgentID>();
       
        // fix this for multiple receivers
        to.add(new AgentID(line,toAddr));
        
        
        boolean notLocal=true;
        //Check toAddr local
        for(int i=0;i<toAddr.size();i++){
        	
        	String[]st=toAddr.get(i).split(":");
        	
        	
        		
        		st[1]=st[1].substring(2);
        		st[2]=st[2].substring(0,st[2].indexOf('/'));
        		int p=Integer.parseInt(st[2]);
        		
        		if(p==fipaPort){
        			InetAddress local=InetAddress.getLocalHost();
        			InetAddress target=InetAddress.getByName(st[1]);
        			if(local.equals(target)){
        				notLocal=false;
        				store.receiveMessage(new IncomingMessage(performative,sender,to,content));
        			}
        		}
        	
        	
        }
        if(notLocal){
        	OutgoingMessage outMessage=new OutgoingMessage(performative,sender,to,content);
        	outMessage.send();
        }
    }
    public void run(){
        try{
            while(true){
                
                Socket socket=buffer.extract();
                System.out.println("socket received");
                DataInputStream dis=null;
                DataOutputStream dos=null;
                try{
                    dis=new DataInputStream(socket.getInputStream());
                    dos=new DataOutputStream(socket.getOutputStream());
                    String line=dis.readUTF();
                    if(line.equals(CHECK_MAIL)){
                        store.sendMessages(dis.readUTF().trim(),dos);
                        dos.flush();
                        receiveMessages(dis,dos);
                        continue;
                    }
                    if(line.equals(REGISTER)){
                        ArrayList<String>agents=new ArrayList<String>();
                        while(!((line=dis.readUTF()).equals(END_REGISTER))){
                            agents.add(line);
                        }
                        store.registerPlatform(dos,agents);
                        receiveMessages(dis,dos);
                        continue;
                    }
                    if(line.equals(DIRECT_MESSAGE)){
                        ArrayList<String>toAddresses=new ArrayList<String>();
                        FOS fos=FOS.createFOS(dis.readUTF());
                        if(fos.hasNext()){
                            while(fos.hasNext())
                                toAddresses.add(fos.next().toString());
                        }else toAddresses.add(fos.toString());
                        String string=dis.readUTF();
                        receive(dis,dos,string,toAddresses);
                        
                        continue;
                    }
                    if(line.equals(REGISTER_AGENT)){
                        store.registerAgent(dis.readUTF().trim(),dis.readUTF().trim());
                    }
                    
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }catch(MalformedLogicException e){
                    e.printStackTrace();
                } finally{
                    try{
                        if(dos!=null)dos.close();
                        if(dis!=null)dis.close();
                        if(socket!=null)socket.close();
                    } catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            }
        }catch(InterruptedException ie){
            
        }
    }
    
}
