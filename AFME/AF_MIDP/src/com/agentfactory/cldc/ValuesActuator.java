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

package com.agentfactory.cldc;
import com.agentfactory.cldc.logic.FOS;


/** The values actuator is enables the agent to update the benefit and 
 * cost values of a commitment. The trigger for the actuator is
 * updateValues(?benefit,?cost,?id). The ?benefit variable represents
 * the new benefit of the commitment. The ?cost variable represents
 * the new cost of the commitment. The ?id variable represents the label
 * of the commitment.
 * 
 * @author Conor Muldoon
 *
 */
public class ValuesActuator extends Actuator {

	AffectManager m;
	/** Creates a new instance of ValuesActuator.
	 * 
	 * @param manager the affect manager of the agent.
	 */
    public ValuesActuator(AffectManager manager) {
        super(manager,"updateValues(?benefit,?cost,?id)");
        m=manager;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	
        m.updateValues(action.next(),action.next(),action.next().toString());
        return true;
    }
}

