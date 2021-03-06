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

package com.agentfactory.cldc.p2pmts;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;


/** The register yellow pages actuator is used when an agent wishes
 * to advertise with the yellow pages that it performs a particular service. The trigger for the register yellow pages actuator is
 * registerYP(?discURL,?service,?name), where the ?discURL variable represents the
 * address of the discovery server, the ?service variable represents the type
 * of service that the agent performs, and the ?name variable represents
 * the name of the agent.
 * @author Conor Muldoon
 *
 */
public class RegYP extends Actuator {

	AffectManager m;
	/** Creates an instance or RegYP.
	 * 
	 * @param manager the affect manager for the agent.
	 */
    public RegYP(AffectManager manager) {
        super(manager,"registerYP(?discURL,?service,?name)");
        m=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	m.scheduleTask(new RegTask(action.next().toString(),action.next().toString(),action.next().toString()));
        return true;
    }
    
    class RegTask implements Runnable{
    	String url,serv,nm;
    	public RegTask(String u,String s,String n){
    		url=u;
    		serv=s;
    		nm=n;
    	}
    	
    	public void run(){
    		try{
    			StreamConnection sc=(StreamConnection)Connector.open(url);
    			DataOutputStream dos=sc.openDataOutputStream();
    			dos.writeByte(DiscServer.REG_YP);
    			dos.writeUTF(serv);
    			dos.writeUTF(nm);
    			
    			dos.close();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    	}
    }
}

