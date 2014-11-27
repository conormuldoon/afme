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


import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.mts.MTSConstant;
/** Adds an agent id to the address list of the agent. This actuator is used
 * when both the standard message transport service and the
 * the peer to peer message transport service are in operation. 
 * To indicate that the peer to peer message transport service is to be
 * used, the functor of the first argument of the trigger must be p2p, otherwise the standard
 * service will be used.
 * The trigger for this actuator is addAgentID(?name,?addresses). The ?name variable
 * is the name of the agent to be added. The ?addresses variable represents
 * the address of the agent to be added.
 * 
 * @author Conor Muldoon
 */
public class DualIDActuator extends Actuator {
    private AffectManager manager;
    
    /** Creates an instance of AddAgentIDActuator.
     * 
     * @param manager the affect manager of the agent.
     */
    public DualIDActuator(AffectManager manager) {
        super(manager,"addAgentID(?name, ?addresses)");
        this.manager=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	FOS fos=action.next();
    	if(fos.functorEquals("p2p"))
        manager.actOn(P2PConstant.MTS,MTSConstant.ADD_ID,FOS.createFOS("addAgentID("+fos.next()+','+action.next()+')'));
    	else{
    		action.reset();
    		manager.actOn(MTSConstant.MTS,MTSConstant.ADD_ID,action);
    	}
        return true;
    }
}
