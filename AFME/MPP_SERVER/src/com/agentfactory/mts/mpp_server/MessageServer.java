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
 * MessageServer.java
 *
 * Created on 08 February 2005, 18:28
 */

package com.agentfactory.mts.mpp_server;

/**
 *
 * @author Conor Muldoon
 */
import java.net.*;
import java.io.*;
import com.agentfactory.mts.mpp_server.fipa.FIPAMessageStore;


public class MessageServer extends Thread{
    
    int port;
    int numWorkers;
    MessageWorker[]worker;
    FIPAMessageStore store;
    int fipaPort;
    
    ServerSocket ss;
    public MessageServer(int port,int numWorkers,FIPAMessageStore store,int fipaPort){
        
        this.store=store;
        this.numWorkers=numWorkers;
        worker=new MessageWorker[numWorkers];
        this.port=port;
        this.fipaPort=fipaPort;
        ss=null;
    }
    public void run(){
        
        System.out.println("Message Server open on "+port);
        try{
            ss=new ServerSocket(port);
            SocketBuffer buffer=new SocketBuffer();
            for(int i=numWorkers;i-->0;)
                (worker[i]=new MessageWorker(buffer,store,fipaPort)).start();
            while(true)buffer.insert(ss.accept());
            
        }catch(java.net.SocketException e){
            System.out.println("Closing Message Server");
        }catch(java.io.IOException ioe){
            System.err.println(ioe.toString());
        }
        
        
        
        
    }
    
    public void kill(){
        
        try{
            ss.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        for(int i=numWorkers;i-->0;)
            worker[i].interrupt();
    }
    
    
    
}
