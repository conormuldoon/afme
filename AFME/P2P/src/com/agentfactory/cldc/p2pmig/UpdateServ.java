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

package com.agentfactory.cldc.p2pmig;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.p2pmts.DiscServer;


/** The update server actuator is used for updating the discover server when an
 * agent migrates to a new platform. 
 * The trigger for the update server actuator is
 * updateServer(?url,?targetPlat,?name), where the ?url variable represents the
 * address of the discovery server, the ?targetPlat variable represents
 * the name of the target platform the agent is migrating to, and the ?name
 * variable represents the name of the agent.
 * 
 * @author Conor Muldoon
 *
 */
public class UpdateServ extends Actuator {

	AffectManager m;
	/** Creates an instance of UpdateServ.
	 * 
	 * @param manager the affect manager for the agent.
	 */
    public UpdateServ(AffectManager manager) {
        super(manager,"updateServer(?url,?targetPlat,?name)");
        m=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	
    	m.scheduleTask(new UpdateTask(action.next().toString(),action.next().toString(),action.next().toString()));
        return true;
    }
    class UpdateTask implements Runnable{
    	String url,tar,name;
    	
    	public UpdateTask(String u,String t,String nm){
    		url=u;
    		tar=t;
    		name=nm;
    	}
    	public void run(){
    		try{
    			StreamConnection sc=(StreamConnection)Connector.open(url);
    			DataOutputStream dos=sc.openDataOutputStream();
    			dos.writeByte(DiscServer.MV);
    			dos.writeUTF(name);
    			dos.writeUTF(tar);
    			dos.close();
    			sc.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }
}

