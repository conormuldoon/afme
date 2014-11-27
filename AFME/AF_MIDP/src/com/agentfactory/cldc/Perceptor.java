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
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.logic.Perceivable;

/** Application developers extend the perceptor class to enable 
 * agents to perceive information related to their state or
 * their environment.
 * 
 * @author Conor Muldoon
 *
 */
public abstract class Perceptor extends Perceivable{


    private PerceptionManager manager;
    /** Creates an instance of Perceptor.
     * @param manager the perception manager of the agent.
     */
    public Perceptor(PerceptionManager manager){
        this.manager=manager;
    }
    

    /** Adds the belief to the agent's current belief set.
     * @param fos the belief to be added.
     */
    public void adoptBelief(FOS fos){
        manager.adoptBelief(fos);
    }
    /** Converts the string to a FOS and adds the 
     * FOS to the agent's current belief set.
     * @param string a string representation of a belief.
     */
    public void adoptBelief(String string){
        try{
            manager.adoptBelief(FOS.createFOS(string));
        }catch(MalformedLogicException e){
            e.printStackTrace();
        }
    }
    
 
}

