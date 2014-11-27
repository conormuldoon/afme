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
 * ServerApp.java
 *
 * Created on 01 July 2005, 11:32
 */

package com.agentfactory.migration.server;
import java.io.IOException;

import java.io.BufferedReader;

import java.io.InputStreamReader;

/**
 *
 * @author Conor Muldoon
 */
public class ServerApp {
    
    static final String FILE_NAME="Server.cfg";
    
    /** Creates a new instance of ServerApp */
    public ServerApp() {
        BufferedReader reader=null;
        try{
            
            reader=new BufferedReader(new InputStreamReader(ServerApp.class.getResourceAsStream(FILE_NAME)));
            String line=null;
            // quick hack for comments fix later
            while((line=reader.readLine()).trim().startsWith("##"));
            
            int midpPort=Integer.parseInt(line);
            int standardPort=Integer.parseInt(reader.readLine());
            int numWorkers=Integer.parseInt(reader.readLine());
            DesignFile standard=new DesignFile(reader.readLine());
            String fileName=reader.readLine();
            
            DesignFile midp=new DesignFile(fileName);
            MigrationStore store=new MigrationStore();
            MigrationServer migServer=new MigrationServer(midpPort,standard,numWorkers,store);
            migServer.start();
            CLDCMigServer cldcServer=new CLDCMigServer(standardPort,midp,numWorkers,store,fileName);    
            cldcServer.start();
            
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                reader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[]args){
        if(args.length==0)new ServerApp();
    }
}
