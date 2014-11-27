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


/** This class is extended by the application developer to create local 
 * platform services. The message transport service is an example of a local
 * platform service. Local platform services are used when agents must share
 * an information space or be capable of accessing a shared object.
 * 
 * @author Conor Muldoon
 *
 */
public abstract class Service{
    private Object name;

    /** Constructs a new instance of Service.
     * @param nm the name of the service.
     */
    public Service(Object nm){
        name=nm;
    }

    /** Adds the name of the service to the specified hash table.
     * @param table the hash table that the name is added to.
     */
    public void register(Hashtable table){
        table.put(name, this);
    }


    /** Changes the name of an agent in the service's registry if one exists.
     * @param oldName the old name of the agent.
     * @param newName the new name of the agent.
     */
    public abstract void modifyBinding(Object oldName, Object newName);

    /** Perceives information from within the service.
     * @param agentName the agent that wishes to perceive the information.
     * @param perceptionID the ID of the information that is to be perceived.
     * @return a FOS that represents the information that has
     * been perceived in the service.
     * @throws MalformedLogicException if there is a logic error.
     */
    public abstract FOS processPer(AgentName agentName,int perceptionID)throws MalformedLogicException;

    /** Performs an action on the service.
     * @param agentName the name of the agent that is performing the action.
     * @param actionID the ID of the action that is to be performed.
     * @param data the data that is to be used in performing the action.
     * @return true if the action was performed successfully, false otherwise.
     * @throws MalformedLogicException if there is a logic error.
     */
    public abstract FOS processAction(AgentName agentName,int actionID,FOS data)throws MalformedLogicException;
    
    /** Called when the platform is terminated.
     * 
     *
     */
    public void destroy(){
    	
    }
}
