// Copyright:   Copyright (c) 1996-2006 The Agent Factory Working Group. All rights reserved.
// Licence:     This file is free software; you can redistribute it and/or modify
//              it under the terms of the GNU Lesser General Public License as published by
//              the Free Software Foundation; either version 2.1, or (at your option)
//              any later version.
//
//              This file is distributed in the hope that it will be useful,
//              but WITHOUT ANY WARRANTY; without even the implied warranty of
//              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//              GNU Lesser General Public License for more details.
//
//              You should have received a copy of the GNU Lesser General Public License
//              along with Agent Factory Micro Edition; see the file COPYING.  If not, write to
//              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//              Boston, MA 02111-1307, USA.


/*
 * FIPAWorker.java
 *
 * Created on 17 February 2005, 16:11
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.StringTokenizer;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.agentfactory.mts.mpp_server.SocketBuffer;



/**
 *
 * @author Conor Muldoon
 */
public class FIPAWorker extends Thread{
    SocketBuffer buffer;
    FIPAMessageStore store;
    
    /** Creates a new instance of FIPAWorker */
    public FIPAWorker(SocketBuffer buffer,FIPAMessageStore store) {
        this.buffer=buffer;
        this.store=store;
        
    }
    public void run(){
        try{
            while(true){
                Socket socket=buffer.extract();
                BufferedReader in = null;
                PrintWriter out = null;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
                    String line=null;
                    String performative=null;
                    FIPAHandler handler=null;
                    String content=null;
                    StringBuilder builder=new StringBuilder();
                    
                    while(!(line=in.readLine()).equals("--boundary--")){
                        
                        if(line.contains("?xml")){
                            builder.append(line);
                            do{
                                line=in.readLine();
                                builder.append(line);
                                builder.append("\n");
                            }while(!line.contains("</envelope>"));
                            
                            InputSource is=new InputSource(new StringReader(builder.toString()));
                            try{
                                XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                                parser.setContentHandler(handler=new FIPAHandler());
                                parser.parse(is);
                                
                            }catch(org.xml.sax.SAXException sax){
                                sax.printStackTrace();
                            }
                            
                        }else if(line.contains(":content")){
                            StringTokenizer tok=new StringTokenizer(line);
                            performative=tok.nextToken().substring(1);
                            tok.nextToken();
                            content=line.substring(line.indexOf(":content")+9, line.length()-1);
                            handler.storeMessage(store,performative,content);
                            
                        }
                    }
                    
                    
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/plain");
                    out.println("Cache-Control: no-cache");
                    out.println("Connection: close");
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    try{
                        if(in!=null)in.close();
                        if(out!=null)out.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch(InterruptedException ie){
            
        }
    }
    
    
    
}
