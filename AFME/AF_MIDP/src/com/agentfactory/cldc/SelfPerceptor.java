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
 * SelfPerceptor.java
 *
 * Created on 02 February 2005, 11:07
 */

package com.agentfactory.cldc;


/** The perceptor enables an agent to adopt a belief regarding
 * its name. The belief adopted is of the form name(?n). The variable
 * ?n represents the name of the agent.
 *
 * @author Conor Muldoon
 */
public class SelfPerceptor extends Perceptor{

    private PerceptionManager manager;

    /** Creates a new instance of SelfPerceptor.
     * 
     * @param manager the perception manager for the agent.
     */
    public SelfPerceptor(PerceptionManager manager) {
        super(manager);
        this.manager=manager;
    }
    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Perceivable#perceive()
     */
    public void perceive(){
        StringBuffer buffer=new StringBuffer("name(");
        manager.appendName(buffer);
        buffer.append(')');
        //System.out.println(buffer.toString());
        adoptBelief(buffer.toString());

    }

}
