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
 * MigrateActuator.java
 *
 * Created on 23 June 2005, 17:23
 */

package com.agentfactory.cldc.p2pmig;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.migration.MigrationConstant;
/** This actuator is used in situations when both the standard migration
 * service and peer to peer migration service are in operation. To indicate
 * that the agent is to migrate using the peer to peer service, the functor
 * of the first argument of the trigger must be p2p, otherwise the standard
 * service will be used.
 * The trigger for this actuator is migrate(?destURL,?url). 
 * The ?destURL variable represents the destination address that the agent
 * wishes to migrate to. The ?url variable represents the address of where
 * the agent's design can be downloaded. The ?url variable is only used when
 * mutation is supported.
 *
 * @author Conor Muldoon
 */
public class DualMigrate extends Actuator{
    AffectManager affManager;
    /** Creates a new instance of DualMigrate.
     * 
     * @param manager the affect manager for the agent.
     */
    public DualMigrate(AffectManager manager) {
        super(manager,"migrate(?destURL,?url)");
        affManager=manager;

    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS fos){
    	FOS f=fos.next();
    	
    	if(f.functorEquals("p2p")){
    		affManager.actOn(P2PMigConstant.MIGRATION, 0, FOS.createFOS("migrate("+f.next().toString()+','+fos.next().toString()+')'));
    	}else {
    		fos.reset();
    		affManager.actOn(MigrationConstant.MIGRATION,0,fos);
    	}
        //System.out.println("migrating");
        return true;
    }
}
