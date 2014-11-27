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
 * UniqueNameService.java
 *
 * Created on 14 April 2005, 11:13
 */

package com.agentfactory.cldc.name_service;
import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
import com.agentfactory.cldc.scheduler.Scheduler;

/** The unique name service is responsible for updating local agent names
 * so as that they are globally unique. A remote server is contacted to
 * obtain a globally unique value. This value is appended to a local agent
 * name to ensure that the name is unique.
 *
 * @author Conor Muldoon
 */

public class UniqueNameService extends Service{
    Object[]agentNames;
    /** Creates a new instance of UniqueNameService.
     * 
     * @param args a string of arguments passed to the service.
     * @param agtNms an array of agent name.
     * @param scheduler a reference to the scheduler.
     */
    public UniqueNameService(String[]args,Object[] agtNms,Scheduler scheduler){
        super(UniqueConstant.UNIQUE);
        agentNames=agtNms;
    }


    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.Service#modifyBinding(java.lang.Object, java.lang.Object)
     */
    public void modifyBinding(Object oldName, Object newName){

    }
    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.Service#processPer(com.agentfactory.cldc.AgentName, int)
     */
    public FOS processPer(AgentName agentName,int perceptionID)throws MalformedLogicException{
        return null;
    }

    /** This method is used to determine what agent names have to be update and subsequently when
     * a unique value has been obtained to update that relevant names.
     * @param agentName the name of the agent calling service.
     * @param actionID the id of the action to perform.
     * @param data the data to be used within an action.
     * @return that data that is to be returned to the calling actuator.
     */
    public FOS processAction(AgentName agentName,int actionID,FOS data)throws MalformedLogicException{
        switch(actionID){

            case UniqueConstant.LOCAL:

                StringBuffer buf=new StringBuffer("loc(");
                boolean b=true;
                for(int i=agentNames.length;i-->0;)
                {
                    AgentName name=(AgentName)agentNames[i];
                    if(name.equals(data)){
                        if(b)b=false;
                        else buf.append(",");
                        StringBuffer buffer=new StringBuffer("localAgent(");
                        data.append(buffer);
                        buffer.append(",");
                        name.appendName(buffer);
                        buffer.append(")");
                        return FOS.createFOS(buffer.toString());

                    }
                }
                if(b)return null;
                buf.append(")");
                return FOS.createFOS(buf.toString());
            case UniqueConstant.UNIQUE_NAME:
                if(agentName.isUnique())return FOS.createFOS("haveName");
                return FOS.createFOS("requestName");

            case UniqueConstant.UPDATE:
                agentName.update((FOS)data);
                return FOS.createFOS("haveName");


        }
        return null;
    }

    /**
     * In the unique name service the run method is blank.
     *
     */
    public void run(){

    }
}


