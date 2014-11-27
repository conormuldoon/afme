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
 * MigrationService.java
 *
 * Created on 29 June 2005, 15:48
 */

package com.agentfactory.migration.server;
import com.agentfactory.platform.service.PlatformService;
import java.net.ServerSocket;
import com.agentfactory.plugins.services.ams.AgentManagementService;
import java.io.IOException;
import com.agentfactory.plugins.services.mts_mpp.SocketBuffer;
import java.util.Map;
import java.util.Hashtable;
/**
 *
 * @author  Conor Muldoon
 */
public class MigrationService extends PlatformService{
    
    AgentManagementService service;
    ServerSocket server;
    SocketBuffer buffer;
    ServiceThread[]thread;
    Map<String,OutgoingAgent>outgoing;
    
    /** Creates a new instance of MigrationService */
    public MigrationService() {
        
    }
    
    public void bind(com.agentfactory.platform.interfaces.Agent agent) {
        
    }
    
    public void migrateAgent(String name){
        service.terminateAgent(name);
        outgoing.get(name).send();
        outgoing.remove(name);
    }
    
    public void init(com.agentfactory.platform.service.PlatformServiceDescriptor descriptor, com.agentfactory.platform.service.PlatformServiceManager manager){
        service = (AgentManagementService)manager.getService("fipa.std.service.ams");
        buffer=new SocketBuffer();
        int i=Integer.parseInt(descriptor.getArgument(1));
        thread=new ServiceThread[i];
        for(int j=i;j-->0;)thread[j]=new ServiceThread(buffer,service);
        try{
            new ServerThread(Integer.parseInt(descriptor.getArgument(0).toString()),buffer).start();
        }catch(IOException e){
            e.printStackTrace();
        }
        outgoing=new Hashtable<String,OutgoingAgent>();
    }
    
    public void addOutgoing(String name,OutgoingAgent outAgt){
        outgoing.put(name, outAgt);
    }
    
    public void modifyBinding(String oldName, String name) {
        
    }
    
    public void start() {
        for(int i=thread.length;i-->0;)thread[i].start();
    }
    
    public void unbind(com.agentfactory.platform.interfaces.Agent agent) {
        
    }
    private class ServerThread extends Thread{
        
        ServerSocket server;
        SocketBuffer buf;
        public ServerThread(int port,SocketBuffer buffer)throws IOException{
            buf=buffer;
            server=new ServerSocket(port);
        }
        public void run(){
            try{
                while(true)buf.insert(server.accept());
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    server.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
