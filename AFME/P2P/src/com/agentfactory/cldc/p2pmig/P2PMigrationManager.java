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

package com.agentfactory.cldc.p2pmig;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.migration.MigrationPlatform;
import com.agentfactory.cldc.migration.OutAgent;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The peer to peer migration manager is a service that is responsible for controlling the
 * migration process in a peer to peer manner. The original migration manner was designed
 * for use in 3G and GPRS environments, whereby service providers operate
 * firewalls. With the peer to peer migration service, agents migrate
 * directly from platform to platform, not via an intermediary server.
 *
 * @author Conor Muldoon
 */


public class P2PMigrationManager extends Service implements Runnable{




    MigrationPlatform plat;
    int size;
    final static byte SIZE=4;
    OutAgent[]outAgt;
    String[]outURL;

    Scheduler sched;
    boolean scheduled;
    /** Creates a new instance of P2PMigrationService.
     * 
     * @param s arguments to the migration service.
     * @param an the names of the agents on the platform.
     * @param scheduler the scheduler periodically executes the run method of 
     * the MigrationManager.
     * @param platform the local migration platform, on which the agents reside.
     */
    public P2PMigrationManager(String[]s,AgentName[]an,Scheduler scheduler,MigrationPlatform platform)   { 
    super(s[0]);

        plat=platform;
        outAgt=new OutAgent[SIZE];
        outURL=new String[SIZE];
        sched=scheduler;
        new ServerThread(s,platform).start();
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
                String[]newURL=new String[outAgt.length<<1];
                System.arraycopy(outAgt,0,newAgt,0,size);
                System.arraycopy(outURL,0,newURL,0,size);
                outAgt=newAgt;
            }
            plat.emigrate(agentName);
            String destURL=data.next().toString();
            outAgt[size]=new OutAgent(agentName,destURL,data.next().toString());
            outURL[size]=destURL;
            size++;
            if(!scheduled){
            	scheduled=true;
            	sched.schedule(this);
            }
        }
        
        return null;
    }


    
   /**
    * Sends outgoing agents to their target platforms.
    */
    public void run(){
        
      

        try{
            
            synchronized(outAgt){
            	scheduled=false;
                
                for(int i=size;i-->0;){
                	StreamConnection conn = (StreamConnection) Connector.open(outURL[i]);
                	DataOutputStream dos=conn.openDataOutputStream();
                	dos.writeInt(1);
                    outAgt[i].write(dos,plat,false);
                    outAgt[i]=null;
                    outURL[i]=null;
                    conn.close();
                    dos.close();
                }

                size=0;
            }

        }catch(javax.microedition.io.ConnectionNotFoundException cnfe ){
            cnfe.printStackTrace();
        }catch( IOException e ){
            e.printStackTrace();
        }catch(ClassCastException e){
        	e.printStackTrace();
        }
    }
}
