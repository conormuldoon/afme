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
 * MigrationController.java
 *
 * Created on 04 July 2005, 17:37
 */

package com.agentfactory.migration.server;
import com.agentfactory.plugins.interpreters.afapl2.control.Controller;
import com.agentfactory.plugins.interpreters.afapl2.AFAPLAgent;
import com.agentfactory.plugins.interpreters.afapl2.control.AgentComponentDescriptor;
import com.agentfactory.platform.util.SystemProperties;
import java.io.IOException;
import java.io.DataInputStream;
/**
 *
 * @author Conor Muldoon
 */
public class MigrationController extends Controller{
    
    boolean migrate;
    AFAPLAgent agt;
    
    public static MigrationController newInstance(AFAPLAgent agent, AgentComponentDescriptor descriptor) {
        MigrationController controller = null;
        
        try {
            controller = (MigrationController) SystemProperties.
                    getInstance().
                    getClass(descriptor.getComponentClass()).
                    newInstance();
            controller.setAgent(agent);
            controller.init(descriptor.getParams());
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
        
        return controller;
    }
    
    public void setAgent(AFAPLAgent agent){
        super.setAgent(agent);
        agt=agent;
    }
    
    public void receiveData(DataInputStream input)throws IOException{
        
        setSleepTime(input.readInt());
    }
    
    public MigrationController() {
        super();
        migrate=false;
    }
    public synchronized void step() {
        super.step();
       
        if(migrate){
            MigrationService service = (MigrationService)agt.getService("cldc.migration");
            service.migrateAgent(agt.getName());
        }
    }
    
    public void migrate(String string){
        migrate=true;
        super.migrate(string);
        
    }
}
