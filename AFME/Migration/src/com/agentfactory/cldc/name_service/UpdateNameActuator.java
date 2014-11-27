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
 * UpdateNameActuator.java
 *
 * Created on 14 April 2005, 11:26
 */

package com.agentfactory.cldc.name_service;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
/** The update name actuator is used to update an agent name with
 * a unique values. So for instance, if the agent's name was Bob and 
 * the unique value was 1, the agent's name would be updated to Bob1.
 *
 * @author Conor Muldoon
 */
public class UpdateNameActuator extends Actuator{
    AffectManager mgr;
    /** Creates a new instance of UpdateNameActuator
     * 
     * @param manager the affect manager for the agent.
     */
    public UpdateNameActuator(AffectManager manager) {
        super(manager,"updateName(?uniqueValue)");
        mgr=manager;
    }

    
    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action){
        adoptBelief(mgr.actOn(UniqueConstant.UNIQUE,UniqueConstant.UPDATE,action.next()));
        return true;
    }

}
