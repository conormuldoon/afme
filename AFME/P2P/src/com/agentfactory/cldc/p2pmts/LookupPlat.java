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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;


/** The lookup platform actuator is used when an agent wishes to obtain the 
 * address of an agent platform. The trigger for the lookup actuator is
 * lookupPlat(?discURL,?platform), where the ?discURL variable represents the
 * address of the discovery server and the ?platform variable represents the
 * name of the platform. The agent adopts the belief platformAddr(?platform,?platURL)
 * once the address for the platform has been obtained.
 * @author Conor Muldoon
 *
 */
public class LookupPlat extends Actuator {

	AffectManager m;
	
	/** Creates an instance of LookupPlat.
	 * 
	 * @param manager the affect manager for the agent.
	 */
    public LookupPlat(AffectManager manager) {
        super(manager,"lookupPlat(?discURL,?platform)");
        m=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	try{
    		StreamConnection sc=(StreamConnection)Connector.open(action.next().toString());
    		DataOutputStream dos=sc.openDataOutputStream();
    		dos.writeByte(DiscServer.REQ_P);
    		String platName=action.next().toString();
    		dos.writeUTF(platName);
    		dos.flush();
    		DataInputStream dis=sc.openDataInputStream();
    		adoptBelief("platformAddr("+platName+','+dis.readUTF()+')');
    		
    		dis.close();
    		dos.close();
    		sc.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
        return true;
    }
}

