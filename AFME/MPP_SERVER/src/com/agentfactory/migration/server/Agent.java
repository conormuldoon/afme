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
 * Agent.java
 *
 * Created on 28 June 2005, 11:32
 */

package com.agentfactory.migration.server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author Conor Muldoon
 */
public class Agent {
    
    final static String ACT="ACT_FACTORY";
    final static String PER="PER_FACTORY";
    final static String MOD="MOD_FACTORY";
    final static String ROLE="USE_ROLE";
    final static String LINK="LINK";
    
    Collection<String>rules;
    Collection<String>beliefs;
    Collection<String>actuators;
    Collection<String>perceptors;
    Collection<String>modules;
    Collection<String>role;
    Collection<AFAPL2Role>afapl2;
    Collection<String>ret;
    
    
    int responseTime;
    int resource;
    String name;
    
    /** Creates a new instance of Agent */
    public Agent(DataInputStream dis,DesignFile designFile)throws IOException{
        
        beliefs=new ArrayList<String>();
        actuators=new ArrayList<String>();
        perceptors=new ArrayList<String>();
        modules=new ArrayList<String>();
        afapl2=new ArrayList<AFAPL2Role>();
        role=new ArrayList<String>();
        ret=new ArrayList<String>();
        //readComponents(dis.readUTF());
        
        // To do: store design url and use it for agent mutation
        dis.readUTF();
        //System.out.println("Design URL "+dis.readUTF());
        
        name=dis.readUTF();
        int numPer=dis.readInt();
        resource=dis.readInt();
        
        rules=designFile.ruleFilter(dis);

        for(int i=numPer;i-->0;)
        	perceptors.add(dis.readUTF());
        
        for(int i=dis.readInt();i-->0;)
        	actuators.add(dis.readUTF());
        
        for(int i=dis.readInt();i-->0;){
        	modules.add(dis.readUTF());
        }
//      read actuator and temporal beliefs
        readBeliefs(dis);
        // read retract beliefs
        for(int j=dis.readInt();j-->0;)
        	ret.add(dis.readUTF());
        
        
        // Read role templates
        int i=dis.readInt();
        while(i-->0){
            afapl2.add(new AFAPL2Role(dis));
            //i=dis.readInt();
        }

        responseTime=dis.readInt();
        System.out.println("Have agent: "+name);
        
    }
    
    public void send(DataOutputStream dos)throws IOException{
        System.out.println("Sending: "+name);
        dos.writeUTF(name);
        dos.writeInt(perceptors.size());
        
        //dos.writeInt(role.size());
        //for(String string:role)dos.writeUTF(string);
        dos.writeInt(resource);
        dos.writeInt(rules.size());
        for(String string:rules)dos.writeUTF(string);
        for(String string:perceptors)dos.writeUTF(string);
        dos.writeInt(actuators.size());
        for(String string:actuators)dos.writeUTF(string);
        dos.writeInt(modules.size());
        for(String s:modules)dos.writeUTF(s);
        dos.writeInt(beliefs.size());
        for(String string:beliefs)dos.writeUTF(string);
        dos.writeInt(ret.size());
        for(String s:ret)dos.writeUTF(s);
        dos.writeInt(afapl2.size());
        for(AFAPL2Role r:afapl2)r.send(dos);
        dos.writeInt(responseTime);
    }
    
   
    
    void readBeliefs(DataInputStream input)throws IOException{
        for(int i=input.readInt();i-->0;)beliefs.add(input.readUTF());
    }
    public void readComponents(String string)throws IOException{
        System.out.println("reading "+string);
        URL url=new URL(string);
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(url.openConnection().getInputStream()));
        String line=null;
        while((line=reader.readLine())!=null){
            String[]s=line.split(" ");
            
            if(s[0].equals(ACT)){
                actuators.add(s[1].substring(0,s[1].length()-1));
                continue;
            }
            if(s[0].equals(PER)){
                perceptors.add(s[1].substring(0,s[1].length()-1));
                continue;
            }
            if(s[0].equals(MOD)){
            	System.out.println("Module functionality changed for AFME, update compiler");
                //modules.add(new Module(s[1],s[2].substring(0,s[2].length()-1)));
                continue;
            }
            if(s[0].equals(ROLE)){
                role.add(s[1].substring(0,s[1].length()-1).replace('.', '/')+".agt");
                continue;
            }
            if(s[0].equals(LINK))readComponents(s[1]);
        }
        reader.close();
    }
    
   
    
}
