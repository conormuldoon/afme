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
 * OutgoingAgent.java
 *
 * Created on 12 July 2005, 18:39
 */

package com.agentfactory.migration.server;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.agentfactory.plugins.interpreters.afapl2.AFAPLAgent;
import com.agentfactory.plugins.interpreters.afapl2.control.BeliefManager;
import com.agentfactory.plugins.interpreters.afapl2.control.RoleLibrary;
import com.agentfactory.plugins.interpreters.afapl2.control.RoleTemplate;
/**
 *
 * @author Conor Muldoon
 */
public class OutgoingAgent {
    String h,plat,u;
    int p;
    AFAPLAgent agt;
    /** Creates a new instance of OutgoingAgent */
    public OutgoingAgent(String platform,String host,int port,String url,AFAPLAgent agent) {
        plat=platform;
        h=host;
        p=port;
        u=url;
        agt=agent;
    }
    
    public void send(){
        Socket s=null;
        DataOutputStream dos=null;
        try{
            s=new Socket(h,p);
            dos=new DataOutputStream(s.getOutputStream());
            dos.writeUTF(plat);
            
            dos.writeInt(agt.getController().getSleepTime());
            dos.writeUTF(u);
            MigrationCommitmentManager mcm=(MigrationCommitmentManager)agt.getCommitmentManager();
            mcm.writeCommitment(dos);
            
            RoleLibrary roleLib=agt.getRoleLibrary();
            Iterator it=roleLib.getRoles();
            
            while(it.hasNext()){
                RoleTemplate rt=(RoleTemplate)it.next();
                Collection terImp=rt.getCommitmentRules();
                
                dos.writeInt(terImp.size());
                Iterator iterator=terImp.iterator();
                while(iterator.hasNext())
                    dos.writeUTF(iterator.next().toString());
                
                
                Collection triggers=rt.getTriggers();
                dos.writeInt(triggers.size());
                iterator=triggers.iterator();
                while(iterator.hasNext())
                    dos.writeUTF(iterator.next().toString());
                dos.writeUTF(rt.getIdentifier().toString());
                
            }
            dos.writeInt(-1);
            BeliefManager manager=agt.getBeliefManager();
            manager.writeAcuatorBeliefs(dos);
           
            Vector beliefs=manager.getTemporalBeliefs();
            int size=beliefs.size();
            dos.writeInt(size);
            for(int i=size;i-->0;)dos.writeUTF(beliefs.get(i).toString());
            String name=agt.getName();
            dos.writeUTF(name);

        }catch(java.net.UnknownHostException e){
            e.printStackTrace(); 
            
        }catch(java.io.IOException e){
            e.printStackTrace();
            
        }
        finally{
            try{
                if(dos!=null)dos.close();
                if(s!=null)s.close();
            }catch(java.io.IOException e){
                e.printStackTrace();
            }
        }
    }
}
