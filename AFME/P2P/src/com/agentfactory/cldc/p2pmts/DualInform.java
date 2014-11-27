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
import com.agentfactory.cldc.mts.Message;
import com.agentfactory.cldc.mts.Sender;



/** The dual inform actuator is used to send inform messages. This actuator is used
 * when both the standard message transport service and the
 * the peer to peer message transport service are in operation. 
 * To indicate that the peer to peer message transport service is to be
 * used, the functor of the first argument of the trigger must be p2p, otherwise the standard
 * service will be used.
 * The trigger
 * for the actuator is inform(?agent,?content). The ?agent variable
 * represents the name of the agent that the message is to be sent to. The
 * ?content variable represents the content of the message.
 * 
 * @author Conor Muldoon
 */
public class DualInform extends Actuator {
    private AffectManager manager;
    /**
     * Creates and instance of DualInform.
     * 
     * @param manager the affect manager of the agent.
     */
    public DualInform(AffectManager manager) {
        super(manager,"inform(?agent,?content)");
        this.manager=manager;
    }

    /** Creates an instance of the inform actuator using the
     * specified affect manager and trigger.
     * 
     * @param manager the affect manager of the agent.
     * @param s a string representation of the trigger of the actuator.
     */
    public DualInform(AffectManager manager,String s) {
        super(manager,s);
        this.manager=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {

    	FOS agt=action.next();
    	if(agt.functorEquals("p2p")){
    		FOS fos=manager.actOn(P2PConstant.MTS, MTSConstant.INFORM, FOS.createFOS("inform("+agt.next()+','+action.next()+')'));
    		if(fos==null)return true;

            return PeerToPeerMTS.send(fos, Message.INFORM);
    	}
    	else{
    		
    		action.reset();
    		FOS fos=manager.actOn(MTSConstant.MTS, MTSConstant.INFORM,action);
    		if(fos==null)return true;
    		return Sender.send(fos,Message.INFORM);
    	}
        
    }
}

