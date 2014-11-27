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
 * FindLocalActuator.java
 *
 * Created on 14 April 2005, 11:36
 */

package com.agentfactory.cldc.name_service;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
/** This actuator obtains a list of names of the agents on the local
 * platform from the unique name service.
 *  The trigger for the actuator is localAgent(?agt).
 *
 * @author  Conor Muldoon
 */
public class LocalNameActuator extends Actuator{

    AffectManager mgr;
    /** Creates a new instance of FindLocalActuator.
     * 
     * @param manager the affect manager for the agent.
     */
    public LocalNameActuator(AffectManager manager) {
        super(manager,"localAgent(?agt)");
        mgr=manager;
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action){
        FOS fos=mgr.actOn(UniqueConstant.UNIQUE, UniqueConstant.LOCAL, action.next());
        if(fos==null)return true;
        for(FOS f=fos.next();f!=null;f=fos.next())adoptBelief(f);
        return true;
    }
}
