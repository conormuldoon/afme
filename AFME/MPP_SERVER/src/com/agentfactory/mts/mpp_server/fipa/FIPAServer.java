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
 * FIPAServer.java
 *
 * Created on 17 February 2005, 16:10
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.net.*;
import java.io.*;

import com.agentfactory.mts.mpp_server.*;

/**
 *
 * @author Conor Muldoon
 */
public class FIPAServer extends Thread{
    
    int port;
    int numWorkers;
    FIPAWorker[]worker;
    ServerSocket ss;
    FIPAMessageStore store;
    
    
    /** Creates a new instance of FIPAServer */
    public FIPAServer(int port,int numWorkers,FIPAMessageStore store) {
        this.numWorkers=numWorkers;
        worker=new FIPAWorker[numWorkers];
        this.port=port;
        ss=null;
        this.store=store;
    }
    
    public void run(){
        System.out.println("FIPA Server open on "+port);
        try{
            ss=new ServerSocket(port);
            SocketBuffer buffer=new SocketBuffer();
            
            for(int i=numWorkers;i-->0;){
                (worker[i]=new FIPAWorker(buffer,store)).start();
            }
            
            while(true){
                buffer.insert(ss.accept());
            }
        }catch(java.net.SocketException e){
            System.out.println("Closing FIPA Server");
        }catch(java.io.IOException ioe){
            ioe.printStackTrace();
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
