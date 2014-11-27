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
 * ControlWorker.java
 *
 * Created on 14 February 2005, 14:31
 */

package com.agentfactory.mts.mpp_server;
import java.net.Socket;
import java.io.*;

/**
 *
 * @author Conor Muldoon
 */
public class ControlWorker extends Thread{
    SocketBuffer buffer;
    
    ControlServer server;
    boolean active;
    /** Creates a new instance of ControlWorker */
    public ControlWorker(SocketBuffer buffer,ControlServer server) {
        this.buffer=buffer;
        this.server=server;
    }
    
    public void run(){
        try{
            while(true){
                Socket socket=buffer.extract();
                
                BufferedReader reader=null;
                try{
                    reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while((line=reader.readLine())!=null){
                        if(line.equals("kill"))
                            server.kill();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                finally{
                    try{
                        if(socket!=null)socket.close();
                        if(reader!=null)reader.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch(InterruptedException e){
           
        }
    }
    
    
    
    
    
}
