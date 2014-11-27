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
 * UniqueNameActuator.java
 *
 * Created on 14 April 2005, 11:25
 */

package com.agentfactory.cldc.name_service;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
/** The unique name actuator causes the belief haveName to be adopted on the next iteration of the control cycle if the agent's
 * name is unique. The belief requestName is adopted if the name is not unique.
 * . The trigger for this actuator is uniqueName.
 * 
 * @author Conor Muldoon
 */
public class UniqueNameActuator extends Actuator{
    AffectManager manager;
    /** Creates a new instance of UniqueNameActuator.
     * 
     * @param mgr the affect manager for the agent.
     */
    public UniqueNameActuator(AffectManager mgr) {
        super(mgr,"uniqueName");
        manager=mgr;
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action){

        adoptBelief(manager.actOn(UniqueConstant.UNIQUE, UniqueConstant.UNIQUE_NAME, null));
        return true;
    }

}
