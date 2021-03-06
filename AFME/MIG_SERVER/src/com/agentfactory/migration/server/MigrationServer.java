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
 * MigrationServer.java
 *
 * Created on 21 June 2005, 14:35
 */

package com.agentfactory.migration.server;

import java.io.IOException;
import java.net.ServerSocket;

import com.agentfactory.mts.mpp_server.SocketBuffer;

/**
 *
 * @author Conor Muldoon
 */
public class MigrationServer extends Thread{
    
    int numWorkers;
    MigrationStore store;
    int port;
    DesignFile midp;
    
    /** Creates a new instance of MigrationServer */
    public MigrationServer(int p,DesignFile midpFile,int workers,MigrationStore migStore) {
        System.out.println("Standard Migration Server open: "+p);
        port=p;
        midp=midpFile;
        store=migStore;
        numWorkers=workers;
    }
    
    
    public void run(){
        ServerSocket ss=null;
        try{
            ss=new ServerSocket(port);
            SocketBuffer buffer=new SocketBuffer();
            for(int i=numWorkers;i-->0;)
                new MigrationWorker(buffer,store,midp).start();
            while(true)buffer.insert(ss.accept());
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                ss.close();
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
