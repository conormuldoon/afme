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
import java.util.Hashtable;

import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;


/** Modules represent an information space that is shared between
 * actuators and perceptors within an agent.
 * 
 * @author Conor Muldoon
 *
 */
public abstract class Module {

    Object id;
    /** Creates an instance of Module.
     * @param identifier the ID of the module.
     */
    public Module(Object identifier){
        id=identifier;
    }

    /** Adds the module to the table using the module ID as the key.
     * @param table the table the module is to be added to.
     */
    public void register(Hashtable table){
        table.put(id,this);
    }

    /** Enables the agent to perceive information within the module.
     * @param perceptionID the ID of the information that is to be perceived.
     * @return a FOS that represents the information that has
     * been perceived in the module.
     * @throws MalformedLogicException if there is a logic error.
     */
    public abstract FOS processPer(int perceptionID)throws MalformedLogicException;
    
    /** Enables agents to perform an action on the module.
     * @param actionID the ID of the action that is to be performed.
     * @param data that data that is to be used in performing the action.
     * @return true if the action was performed successfully, false otherwise.
     * @throws MalformedLogicException if there is a logic error.
     */
    public abstract FOS processAction(int actionID,FOS data)throws MalformedLogicException;

}
