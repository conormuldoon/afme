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

 package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.PerceptionManager;
import com.agentfactory.cldc.Perceptor;
import com.agentfactory.cldc.logic.FOS;
/** The MTSPerceptor perceives information from the message transport
 * service. This information includes messages that have been sent to
 * the agent, the agent IDs of agents that the agent communicates with,
 * the local residents on the platform, and pending messages.
 * 
 * @author Conor Muldoon
 */
public class MTSPerceptor extends Perceptor {
    private PerceptionManager manager;

    /** Creates an instance of MTSPerceptor.
     * 
     * @param manager the perception manager for the agent.
     */
    public MTSPerceptor(PerceptionManager manager){
        super(manager);
        this.manager=manager;
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Perceivable#perceive()
     */
    public void perceive() {

    	perceive(manager.perManage(MTSConstant.MTS,MTSConstant.MESSAGE));
    	perceive(manager.perManage(MTSConstant.MTS, MTSConstant.ID));
    	perceive(manager.perManage(MTSConstant.MTS, MTSConstant.RESIDENT));
    	perceive(manager.perManage(MTSConstant.MTS,MTSConstant.PENDING));

    }
    
    private void perceive(FOS fos){
    	if(fos!=null)for(FOS f=fos.next();f!=null;f=fos.next()){
            adoptBelief(f);
    	}
    }
}
