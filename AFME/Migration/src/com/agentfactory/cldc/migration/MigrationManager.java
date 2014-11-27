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
 * Created on 20 June 2005, 13:06
 */

package com.agentfactory.cldc.migration;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Platform;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The migration manager is a service that is responsible for controlling the
 * migration process. 
 *
 * @author Conor Muldoon
 */


public class MigrationManager extends Service implements Runnable{

    final static String REG="-1";
    String url;
    boolean checkReg;
    //final String storeName;
    String identifier;
    MigrationPlatform plat;
    int size;
    final static byte SIZE=4;
    OutAgent[]outAgt;
    private static final String MIG="Mig";


    /** Creates a new instance of MigrationService.
     * 
     * @param s arguments to the migration service.
     * @param an the names of the agents on the platform.
     * @param scheduler the schduler periodically executes the run method of 
     * the MigrationManager.
     * @param platform the local migration platform, on which the agents reside.
     */
    //public MigrationManager(String u,MigrationPlatform platform,String id,Scheduler scheduler,int period) {
    public MigrationManager(String[]s,AgentName[]an,Scheduler scheduler,MigrationPlatform platform)   { 
    super(MigrationConstant.MIGRATION);
    
        scheduler.schedule(MigrationConstant.MIGRATION, this, Integer.parseInt(s[0]));
        url=s[1];

        
        checkReg=true;
        plat=platform;
        outAgt=new OutAgent[SIZE];

        identifier=s[2];

    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.Service#modifyBinding(java.lang.Object, java.lang.Object)
     */
    public void modifyBinding(Object oldName, Object newName){

    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.Service#processPer(com.agentfactory.cldc.AgentName, int)
     */
    public FOS processPer(AgentName agentName,int perceptionID){
        return null;
    }

    /** Add the agent  with the specified name to the outgoing list.
     * @param agentName the name of the agent to be added to the outgoing list.
     * @param actionID the actionID is not used in this instance of processAction.
     * @param data represents the destination and design URL of the agent.
     */
    public FOS processAction(AgentName agentName,int actionID,FOS data){
        synchronized(outAgt){
            if(size==outAgt.length){
            	
                OutAgent[]newAgt=new OutAgent[outAgt.length<<1];
                System.arraycopy(outAgt,0,newAgt,0,size);
                
                outAgt=newAgt;
            }
            plat.emigrate(agentName);
            outAgt[size]=new OutAgent(agentName,data.next().toString(),data.next().toString());
            
            size++;
        }
        
        return null;
    }

   /** The run method periodically connections to the MigrationServer.
    * It registers the platform with the server the first time the
    * platform is used. It subsequently checks the server for incomming 
    * agents and transfers outgoing agents.
    */
    public void run(){
        if(checkReg&&identifier.equals(REG)){
            checkReg=false;
            identifier=((Platform)plat).newStore(MIG);
            
        }
        StreamConnection conn = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try{
            conn = (StreamConnection) Connector.open(url);
            dos=conn.openDataOutputStream();
            synchronized(outAgt){

                dos.writeInt(size);
                for(int i=size;i-->0;){
                    outAgt[i].write(dos,plat,true);
                    outAgt[i]=null;
                }

                size=0;
            }
            
            dis=conn.openDataInputStream();
            if(identifier.equals(REG)){
                dos.writeUTF(MigrationConstant.REGISTER);
                dos.flush();

                identifier=String.valueOf(dis.readLong()).trim();

                ((Platform)plat).addData(identifier,MIG);

            }
            
            dos.writeUTF(identifier);
            dos.flush();
            
            for(int i=dis.readInt();i-->0;)plat.createAgent(dis);
            
            conn.close();
            dis.close();
            dos.close();
        }catch(javax.microedition.io.ConnectionNotFoundException cnfe ){
            cnfe.printStackTrace();
        }catch( IOException e ){
            e.printStackTrace();
        }catch(ClassCastException e){
        	e.printStackTrace();
        }
    }
}
