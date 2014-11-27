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
 * DeactivateRoleActuator.java
 *
 * Created on 08 August 2005, 18:33
 */

package com.agentfactory.cldc;

import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;


/** Activates a role within the agent. The trigger for this actuator
 * is enactRole(?role). The ?role variable represents the ID of the
 * role that is to be activated.
 *
 * @author Conor Muldoon
 */
public class EnactRoleActuator extends Actuator{
    AffectManager manager;
    /** Creates a new instance of DeactivateRoleActuator
     * 
     * @param affectManager the affect manager for the agent.
     */
    public EnactRoleActuator(AffectManager affectManager) {
        super(affectManager,"enactRole(?role)");
        manager=affectManager;
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action){
        try{
            manager.enactRole(action.next());
        }catch(MalformedLogicException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
