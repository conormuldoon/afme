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
 * ModuleFactory.java
 *
 * Created on 20 June 2005, 15:02
 */

package com.agentfactory.cldc;

/** This class is extended by factories that are used to create modules. 
 * Factories are only required when migration is being used.
 *
 * @author Conor Muldoon
 */

public interface ModuleFactory {

    /** Factory classes implement this interface to create a module in 
     * the migration process. Each module that is created when an agent migrates
     * on to the platform will require its own factory class.
     * @param an the name of the agent that the module belongs to.
     * @return the created module.
     */
    public Module createModule(AgentName an);

}
