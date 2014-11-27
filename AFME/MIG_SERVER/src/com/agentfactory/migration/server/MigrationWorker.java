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
 * MigrationWorker.java
 *
 * Created on 21 June 2005, 14:36
 */

package com.agentfactory.migration.server;
import com.agentfactory.mts.mpp_server.SocketBuffer;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
/**
 *
 * @author Conor Muldoon
 */
public class MigrationWorker extends Thread{
    SocketBuffer buf;
    MigrationStore store;
    
    DesignFile designFile;
    
    /** Creates a new instance of MigrationWorker */
    public MigrationWorker(SocketBuffer buffer,MigrationStore s,DesignFile file) {
        buf=buffer;
        store=s;
        designFile=file;
    }
    
    
    public void run(){
        try{
            while(true){
                
                DataInputStream dis=null;
                Socket socket=buf.extract();
                System.out.println("socket received");
                try{
                    dis=new DataInputStream(socket.getInputStream());
                    store.storeAgent(dis.readUTF(),new Agent(dis,designFile));
                }catch(IOException e){
                    e.printStackTrace();
                }finally{
                    try{
                        dis.close();
                        socket.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }              
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}