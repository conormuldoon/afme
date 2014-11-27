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
 * OutAgent.java
 *
 * Created on 29 June 2005, 18:12
 */

package com.agentfactory.cldc.migration;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import com.agentfactory.cldc.AgentName;

/** The OutAgent class represents an agent that will migrate from the local 
 * platform. It contains the agent name, the destination address, and the URL
 * of where the agent's design is located.
 *
 * @author Conor Muldoon
 */
public class OutAgent {

    AgentName name;
   

    String destination;
    String designURL;

    /** Creates a new instance of OutAgent.
     * 
     * @param agtName the name of the agent.
     * @param url the agent's destination.
     * @param design the URL of where the agent's design may be obtained.
     */
    public OutAgent(Object agtName,String url,String design) {
        name=(AgentName)agtName;
        destination=url;
        designURL=design;
    }

    /** Causes the destination, design URL, and the agent's state to be 
     * written to a data output stream.
     * 
     * @param output the specified data output stream.
     * @param plat the migration platform, which the agent currently resides on.
     * @throws IOException if there is an I/O error.
     */
    public void write(DataOutput output, MigrationPlatform plat,boolean b)throws IOException{
    	if(b){
    		output.writeUTF(destination);
    		output.writeUTF(designURL);
    	}
    	name.write(output);
        plat.writeAgent(name,output);
        
        plat.writeRes(name,output);
        //agt.writeToStream(output);
        //name.write(output);
        //output.writeInt(period);
    }
    
    /** Writes the agent's name to a data output stream.
     * 
     * @param output the specified data output stream.
     * @throws IOException if there is an I/O error.
     */
    public void writeName(DataOutputStream output)throws IOException{
    	name.write(output);
    }
    
    

}