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
 * MPPServer.java
 *
 * Created on 14 February 2005, 15:20
 */

package com.agentfactory.mts.mpp_server;

import com.agentfactory.mts.mpp_server.fipa.FIPAServer;
import com.agentfactory.mts.mpp_server.fipa.FIPAMessageStore;

/**
 *
 * @author Conor Muldoon
 */
public class MPPServer {
    MessageServer messServ;
    FIPAServer fipaServ;
    public MPPServer(MessageServer messServ,FIPAServer fipaServ,int port,int num){
        this.messServ=messServ;
        this.fipaServ=fipaServ;
        ControlServer connServ=new ControlServer(port,num,this);
        
        messServ.start();
        fipaServ.start();
        connServ.start();
        
    }
    
    public void kill(){
        
        messServ.kill();
        fipaServ.kill();
    }
    
    public static void main(String[]args){
        int numWorkers=4;
        int numConWorkers=1;
        int conPort=4446;
        int port=4445;
        int fipaPort=4447;
        
        if(args.length>0){
            port=Integer.parseInt(args[0]);
            if(args.length>1){
                numWorkers=Integer.parseInt(args[1]);
                if(args.length>2){
                    fipaPort=Integer.parseInt(args[2]);
                    if(args.length==4)numConWorkers=Integer.parseInt(args[3]);
                }
            }
        }
        
        FIPAMessageStore store=new FIPAMessageStore(fipaPort);
        new MPPServer(new MessageServer(port,numWorkers,store,fipaPort),new FIPAServer(fipaPort,4,store),conPort,numConWorkers);
        
    }
    
}
