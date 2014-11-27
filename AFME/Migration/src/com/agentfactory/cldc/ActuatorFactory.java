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
 * ActuatorFactory.java
 *
 * Created on 20 June 2005, 15:01
 */

package com.agentfactory.cldc;

/** Developers extend this class to create factories that enable
 * actuators to be created. Factories are only required when migration is being used.
 *
 * @author Conor Muldoon
 */

public interface ActuatorFactory {

    /** Factory classes implement this interface to create an actuator 
     * in the migration process. Each actuator that is created when an agent
     * migrates on to a platform, will require its own factory. 
     * The manager is passed to the actuator's constructor.
     * @param manager the affect manager for the agent.
     * @return an actuator that was created by the factory.
     */
    public Actuator createActuator(AffectManager manager);
}
