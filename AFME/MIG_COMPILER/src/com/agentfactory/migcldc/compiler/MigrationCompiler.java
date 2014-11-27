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
 * MigrationCompiler.java
 *
 * Created on 08 July 2005, 16:03
 */

package com.agentfactory.migcldc.compiler;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URLConnection;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Conor Muldoon
 */
public class MigrationCompiler{
    
    static final String STANDARD="/s";
    /** Creates a new instance of MigrationCompiler */
    public MigrationCompiler() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[]classpath=args[0].split(";");
       
        int len=classpath.length;
        URL[]url=new URL[len];
        
        try{
            for(int i=len;i-->0;)url[i]=new File(classpath[i]).toURL();
            Collection<String>output=new ArrayList<String>();
            String fileName=args[1];
            String mode=args[2];
            compile(fileName,new URLClassLoader(url),mode,output);
            fileName.lastIndexOf('.');
            
            File file=new File(fileName.substring(0,
                    fileName.lastIndexOf('.'))+((mode.equals(STANDARD))?"_S":"_C")+".afapl2");
            
            PrintWriter pw=new PrintWriter(new FileWriter(file));
            for(String string:output)pw.println(string);
            
            pw.close();
            
        }catch(java.net.MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    static void compile(String fileName,ClassLoader loader,String mode,Collection<String>coll)throws IOException{
        
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(loader.getResourceAsStream(fileName)));
        String line=null;
        
        while((line=reader.readLine())!=null){
            String[]string=line.split("* *");
            String command=string[0];
            int n=string.length;
            int i=0;
            if(command.trim().equals(""))i++;
            if(i==n)continue;
            command=string[i];
           
            if(command.equals("MROLE"))compile(string[i+1],loader,mode,coll);
            else if(mode.equals(STANDARD)){
                if(command.equals("CLDC"))continue;
                if(command.equals("STANDARD")){
                    
                    BufferedReader br=createReader(string[i+1]);
                    String con=null;
                    while((con=br.readLine())!=null){
                        String[]str=con.split(" ");
                        if(str[0].equals("LINK"))continue;
                        coll.add(con);
                    }
                    br.close();
                }
                else coll.add(line);
            }else{
                if(command.equals("STANDARD"))continue;
                if(command.equals("CLDC")){
                    BufferedReader br=createReader(string[i+1]);
                    String con=null;
                    while((con=br.readLine())!=null){
                        String[]str=con.split(" ");
                        if(str[0].equals("LINK")||
                                str[0].equals("ACT_FACTORY")||
                                str[0].equals("PER_FACTORY")||
                                str[0].equals("MOD_FACTORY"))
                            continue;
                        coll.add(con);
                    }
                    br.close();
                }else coll.add(line);
            }
        }
        reader.close();
    }
    static BufferedReader createReader(String string)throws IOException{
        
        URL url=new URL(string);
        URLConnection connection=url.openConnection();
        BufferedReader br=new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        return br;
    }
}