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
package com.agentfactory.cldc.mts;

import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;


/** The inform actuator is used to send inform messages. The trigger
 * for the actuator is inform(?agent,?content). The ?agent variable
 * represents the name of the agent that the message is to be sent to. The
 * ?content variable represents the content of the message.
 * 
 * @author Conor Muldoon
 */
public class InformActuator extends Actuator {
    private AffectManager manager;
    /**
     * Creates and instance of InformActuator.
     * 
     * @param manager the affect manager of the agent.
     */
    public InformActuator(AffectManager manager) {
        super(manager,"inform(?agent,?content)");
        this.manager=manager;
    }

    /** Creates an instance of the inform actuator using the
     * specified affect manager and trigger.
     * 
     * @param manager the affect manager of the agent.
     * @param s a string representation of the trigger of the actuator.
     */
    public InformActuator(AffectManager manager,String s) {
        super(manager,s);
        this.manager=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {

        FOS fos=manager.actOn(MTSConstant.MTS, MTSConstant.INFORM, action);
        if(fos==null)return true;
        return Sender.send(fos, Message.INFORM);
    }
}

