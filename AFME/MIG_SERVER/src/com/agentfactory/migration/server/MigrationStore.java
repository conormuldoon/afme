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
 * MigrationStore.java
 *
 * Created on 28 June 2005, 11:31
 */

package com.agentfactory.migration.server;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
/**
 *
 * @author Conor Muldoon
 */
public class MigrationStore {
    
    Map<String,Collection<Agent>>platformMap;
    File file;
    long num;
    
    /** Creates a new instance of MigrationStore */
    public MigrationStore() {
        platformMap=new HashMap<String,Collection<Agent>>();
        file=new File("MigationPlatNum.bin");
        DataInputStream dis=null;
        DataOutputStream dos=null;
        
        try{
            if(!file.exists()){
                file.createNewFile();
                num=0;
                dos=new DataOutputStream(new FileOutputStream(file));
                dos.writeLong(-1);
            }else{
                dis=new DataInputStream(new FileInputStream(file));
                num=dis.readLong();
                num++;
                for(long l=num;l-->0;)
                    platformMap.put(String.valueOf(l),new ArrayList<Agent>());
            }
        }catch(IOException e){
            e.printStackTrace();
            
        }finally{
            try{
                if(dis!=null)dis.close();
                if(dos!=null)dos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    
    public synchronized void storeAgent(String platform,Agent agent)throws IOException{
        Collection<Agent>coll=platformMap.get(platform);
        
        if(coll==null){
            newPlat(platform);
            return;
        }
        coll.add(agent);
        System.out.println("agent stored");
    }
    
    void newPlat(String string)throws IOException{
        platformMap.put(string,new ArrayList<Agent>());
        int val=Integer.parseInt(string);
        if(val>=num){
            num=val;
            save();
        }
    }
    
    public synchronized void sendAgents(String string,DataOutputStream dos)throws IOException{
        
        Collection<Agent>coll=platformMap.get(string);
        if(coll==null){
            newPlat(string);
            return;
        }
        dos.writeInt(coll.size());
        for(Agent agent:coll)agent.send(dos);
        coll.clear();
    }
    void save()throws IOException{
        DataOutputStream dos=null;
        try{
            dos=new DataOutputStream(new FileOutputStream(file));
            dos.writeLong(num);
            num++;
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(dos!=null)dos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void registerNew(DataOutputStream output)throws IOException{
        System.out.println("Registering Platform: "+num);
        platformMap.put(String.valueOf(num),new ArrayList<Agent>());
        output.writeLong(num);
        output.flush();
        save();
        
    }
    
}
