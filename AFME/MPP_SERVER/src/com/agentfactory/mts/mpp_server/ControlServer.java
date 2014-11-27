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


public class ControlServer extends Thread{
    
    int port,numWorkers;
    MPPServer mppServer;
    ServerSocket ss;
    ControlWorker[]worker;
    public ControlServer(int port,int numWorkers,MPPServer mppServer){
        this.port=port;
        worker=new ControlWorker[numWorkers];
        this.numWorkers=numWorkers;
        this.mppServer=mppServer;
        ss=null;
        
    }
    
    public void run(){
    	/*
        try{
            System.out.println("Control Server open on "+port);
            ss=new ServerSocket(port);
            SocketBuffer buffer=new SocketBuffer();
            
            for(int i=numWorkers;i-->0;){
                (worker[i]=new ControlWorker(buffer,this)).start();
            }
            
            while(true){
                buffer.insert(ss.accept());
            }
        }catch(java.net.SocketException e){
            System.out.println("Closing Control Server");
        }catch(java.io.IOException ioe){
            ioe.printStackTrace(System.err);
        }*/
        
        
    }
    
    public void kill(){
        mppServer.kill();
        try{
            
            ss.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        for(int i=numWorkers;i-->0;){
            worker[i].interrupt();
        }
        
        
    }
    
    
}
