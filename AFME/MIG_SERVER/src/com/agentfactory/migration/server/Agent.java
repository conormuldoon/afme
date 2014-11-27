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
    Collection<Module>modules;
    Collection<String>role;
    Collection<AFAPL2Role>afapl2;
    
    
    int responseTime;
    String name;
    
    /** Creates a new instance of Agent */
    public Agent(DataInputStream dis,DesignFile designFile)throws IOException{
        
        beliefs=new ArrayList<String>();
        actuators=new ArrayList<String>();
        perceptors=new ArrayList<String>();
        modules=new ArrayList<Module>();
        afapl2=new ArrayList<AFAPL2Role>();
        role=new ArrayList<String>();
        responseTime=dis.readInt();
        readComponents(dis.readUTF());
        
        int i=dis.readInt();
        while(i!=-1){
            afapl2.add(new AFAPL2Role(i,dis));
            i=dis.readInt();
        }
        
        rules=designFile.ruleFilter(dis);
        //read actuator beliefs
        readBeliefs(dis);
         //read temporal beliefs
        readBeliefs(dis);
        
        name=dis.readUTF();
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
                modules.add(new Module(s[1],s[2].substring(0,s[2].length()-1)));
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
    
    public void send(DataOutputStream dos)throws IOException{
        
        dos.writeUTF(name);
        dos.writeInt(perceptors.size());
        
        dos.writeInt(role.size());
        for(String string:role)dos.writeUTF(string);
        
        dos.writeInt(rules.size());
        for(String string:rules)dos.writeUTF(string);
        for(String string:perceptors)dos.writeUTF(string);
        dos.writeInt(actuators.size());
        for(String string:actuators)dos.writeUTF(string);
        dos.writeInt(modules.size());
        for(Module module:modules)module.send(dos);
        dos.writeInt(beliefs.size());
        for(String string:beliefs)dos.writeUTF(string);
        dos.writeInt(afapl2.size());
        for(AFAPL2Role r:afapl2)r.send(dos);
        dos.writeInt(responseTime);
    }
    
}
