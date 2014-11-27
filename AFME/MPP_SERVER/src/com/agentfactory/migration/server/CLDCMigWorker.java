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
 * CLDCMigWorker.java
 *
 * Created on 28 June 2005, 11:31
 */

package com.agentfactory.migration.server;
import com.agentfactory.mts.mpp_server.SocketBuffer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import com.agentfactory.cldc.migration.MigrationConstant;
import java.net.Socket;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 *
 * @author Conor Muldoon
 */
public class CLDCMigWorker extends Thread{
    
    final static String ACT="ACTUATOR";
    final static String PER="PERCEPTOR";
    final static String MOD="LOAD_MODULE";
    final static String CON="CONTROLLER";
    final static String SERV="SERVICE_BIND";
    final static String ROLE="USE_ROLE";
    final static String IMP="IMPORT";
    final static String RL="ROLE";
    final static String LINK="LINK";
    
    SocketBuffer socketBuffer;
    MigrationStore ms;
    DesignFile file;
    
    
    /** Creates a new instance of CLDCMigWorker */
    public CLDCMigWorker(SocketBuffer buffer,MigrationStore store,DesignFile designFile) {
        socketBuffer=buffer;
        ms=store;
        file=designFile;
        
    }
    
    public void run(){
        try{
            while(true){
                Socket socket=socketBuffer.extract();
                DataInputStream dis=null;
                DataOutputStream output=null;
                try{
                    dis=new DataInputStream(socket.getInputStream());
                    Socket soc=null;
                    for(int i=dis.readInt();i-->0;){
                        DataOutputStream dos=null;
                        try{
                            String[]s=dis.readUTF().split(":");
                            String host=s[0];
                            int port=Integer.parseInt(s[1]);
                            System.out.println("Sending Agent to: "+host+":"+port);
                            soc=new Socket(host,port);
                            
                            dos=new DataOutputStream(soc.getOutputStream());
                            
                            // read design url
                            Collection<String>design=new ArrayList<String>();
                            Collection<String>role=new ArrayList<String>();
                            Collection<String>imp=new ArrayList<String>();
                            readURLFile(dis.readUTF(),design,role,imp);
                            
                            // filter commitment rules
                            Collection<String>rule=file.ruleFilter(dis);
                            
                            // send design
                            dos.writeInt(design.size()+rule.size());
                            for(String string:design){
                            	dos.writeUTF(string);
                            	//System.out.println(string);
                            }
                            for(String string:rule)dos.writeUTF(string);
                            
                            //for(String string:design)System.out.println(string);
                            //for(String string:rule)System.out.println(string);
                            
                            //send role
                            dos.writeInt(role.size());
                            for(String string:role)dos.writeUTF(string);
                            
                            //send import
                            dos.writeInt(imp.size());
                            for(String string:imp)dos.writeUTF(string);
                            
                            // construct and send design
                            // send temporal beliefs
                            int numBeliefs=dis.readInt();
                            dos.writeInt(numBeliefs);
                            for(int j=numBeliefs;j-->0;)dos.writeUTF(dis.readUTF());
                            
                            // send retract beliefs
                            int retNum=dis.readInt();
                            dos.writeInt(retNum);
                            for(int j=retNum;j-->0;)dos.writeUTF(dis.readUTF());
                            
                            // send afapl2 templates
                            int numTemp=dis.readInt();
                            dos.writeInt(numTemp);
                            for(int j=numTemp;j-->0;){
                                dos.writeUTF(dis.readUTF());
                                forwardArray(dis,dos);
                                forwardArray(dis,dos);
                                
                            }
                                 
                            // send agent name
                            dos.writeUTF(dis.readUTF());
                           
                            // send response time
                            dos.writeInt(dis.readInt());
                        }catch(IOException e){
                            e.printStackTrace();
                        }finally{
                            if(dos!=null)dos.close();
                            if(soc!=null)soc.close();
                        }
                        System.out.println("agent sent");
                        
                    }
                    
                    String line=dis.readUTF();
                    output=new DataOutputStream(socket.getOutputStream());
                    if(line.equals(MigrationConstant.REGISTER)){
                        ms.registerNew(output);
                        line=dis.readUTF();
                    }
                    //System.out.println(line+" checking");
                    ms.sendAgents(line,output);
                    
                    output.close();
                    
                }catch(IOException e){
                    e.printStackTrace();
                    
                } finally{
                    try{
                        if(dis!=null)dis.close();
                        if(output!=null)output.close();
                        if(socket!=null)socket.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    
    void forwardArray(DataInputStream input,DataOutputStream output)throws IOException{
        int num=input.readInt();
        output.writeInt(num);
        for(int i=num;i-->0;)
            output.writeUTF(input.readUTF());
    }
    
    public void readURLFile(String u,Collection<String>design,Collection<String>role,Collection<String>imp)throws IOException{
        URL url=new URL(u);
        System.out.println("reading "+u);
        URLConnection connection=url.openConnection();
        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line=null;
        while((line=reader.readLine())!=null){
        	line=line.trim();
            String[]s=line.split(" ");
            
            if(s[0].equals(ACT))design.add(line);
            else if(s[0].equals(PER))design.add(line);
            else if(s[0].equals(MOD))design.add(line);
            else if(s[0].equals(SERV))design.add(line);
            else if(s[0].equals(CON)){
            	design.add(line);
            	//System.out.println("Adding controller "+line);
            }
            else if(s[0].equals(ROLE))role.add(s[1].substring(0,s[1].length()-1).replace('.','/')+".agt");
            else if(s[0].equals(IMP))imp.add(s[1].substring(0,s[1].length()-1).replace('.','/')+".agent");
            else if(s[0].equals(LINK))readURLFile(s[1],design,role,imp);
        }
        reader.close();
        
    }
    
}
