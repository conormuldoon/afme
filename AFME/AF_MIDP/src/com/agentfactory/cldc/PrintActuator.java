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

/** Enables an agent to print a message to the console. 
 * The trigger for this actuator is print(?text), where
 * the ?text variable represents the string that is to be
 * displayed.
 * 
 * @author Conor Muldoon
 */
public class PrintActuator extends Actuator {

	/** Creates an instance of PrintActuator.
	 * 
	 * @param manager the affect manager for the agent.
	 */
    public PrintActuator(AffectManager manager) {
        super(manager,"print(?text)");       
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {
    	
    	System.out.println(action.next());
        return true;
    }
}

