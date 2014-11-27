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
 * RegisterAgentActuator.java
 *
 * Created on 15 April 2005, 19:38
 */

package com.agentfactory.cldc.name_service;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.mts.MTSConstant;
import com.agentfactory.cldc.mts.ServerConstant;
/** Registers the agent with the message server. The trigger for this actuator is
 * registerAgent. Agents must register with the message server when their
 * name has been appended with a unique value so as to enable messages to 
 * be forwarded appropriately.
 *
 * @author Conor Muldoon
 */
public class RegisterAgentActuator extends Actuator{
    AffectManager mgr;
    /** Creates a new instance of RegisterAgentActuator.
     * @param manager the affect manager for the agent.
     */
    public RegisterAgentActuator(AffectManager manager) {
        super(manager,"registerAgent");
        mgr=manager;
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action){
        FOS fos=mgr.actOn(MTSConstant.MTS, MTSConstant.REGISTER_AGENT, null);
        try{
            StreamConnection conn = (StreamConnection) Connector.open(fos.next().toString());
            DataOutputStream dos=conn.openDataOutputStream();
            DataInputStream dis=conn.openDataInputStream();
            dos.writeUTF(ServerConstant.REGISTER_AGENT);
            dos.writeUTF(fos.next().toString());
            dos.writeUTF(fos.next().toString());
            conn.close();
            dis.close();
            dos.close();
        }catch( ConnectionNotFoundException cnfe ){
            cnfe.printStackTrace();
            return false;
        }catch( IOException e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
