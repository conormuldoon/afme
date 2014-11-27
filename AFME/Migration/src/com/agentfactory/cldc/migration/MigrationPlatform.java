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
 * MigrationPlatform.java
 *
 * Created on 20 June 2005, 14:00
 */

package com.agentfactory.cldc.migration;
import java.io.DataInput;
import java.io.IOException;

import com.agentfactory.cldc.AgentName;


/** Platforms that support migration must implement the migration platform 
 * interface. It contains methods create agents on the local platform and 
 * moving agent's to a destination platform.
 *
 * @author Conor Muldoon
 */

public interface MigrationPlatform {

	/** Creates an agent from an input stream.
	 * 
	 * @param dis the specified data input stream.
	 * @throws IOException if there is an I/O error.
	 */
    public void createAgent(DataInput dis)throws IOException;
    
    /** Causes an agent to leave the local platform.
     * 
     * @param name the name of the agent that is to migrate.
     */
    public void emigrate(AgentName name);
    
    /** Writes the state of the agent that is associated with the specified
     * key in the scheduler to be written to a data output stream.
     * 
     * @param key the key that is associated with the agent in the scheduler.
     * @param dos the data output stream to which the agent's state is to
     * be written.
     * @throws IOException if there is an I/O error.
     */
    public void writeAgent(Object key,java.io.DataOutput dos)throws IOException;
    
    /** Writes the agent's resource constraint to a data output stream.
     * 
     * @param key the key that is associated with the agent in the scheduler.
     * @param dos the data output stream to which the agent's resource constraint is to
     * be written.
     * @throws IOException if there is an I/O error.
     */
    public void writeRes(Object key,java.io.DataOutput dos)throws IOException;

}
