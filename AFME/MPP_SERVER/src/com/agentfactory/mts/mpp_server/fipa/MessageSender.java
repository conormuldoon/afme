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
 * Sender.java
 *
 * Created on 18 February 2005, 11:10
 */

package com.agentfactory.mts.mpp_server.fipa;
import java.net.*;
import java.io.*;
import static com.agentfactory.mts.mpp_server.fipa.Constant.*;
import java.util.StringTokenizer;

/**
 *
 * @author Conor Muldoon
 */
public class MessageSender {
    
    public static final String BOUNDARY = "boundary";
    
    public static void sendMessage(String url, OutgoingMessage message) {
        
        
        BufferedReader in=null;
        PrintWriter out=null;
        Socket s=null;
        String[]str=url.split(":");
        
        if(str[0].equals("http"))try {
            String host=str[1].substring(2);
            int port=Integer.parseInt(str[2].substring(0,str[2].length()-4));
            s = new Socket(str[1].substring(2),port);
            out = new PrintWriter(new BufferedOutputStream(s.getOutputStream()), false);
            in= new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.println("connecting to "+url);
            
            // Sending header!
            out.print("POST " + url.toString() + " HTTP/1.1");
            out.print(CRLF);
            out.print("Cache-Control: no-cache");
            out.print(CRLF);
            out.print("Host: " + host + ":" + port);
            out.print(CRLF);
            out.print("MIME-Version: 1.0");
            out.print(CRLF);
            out.print("Content-Type: multipart/mixed;");
            out.print(CRLF);
            out.print(" boundary=\"" + BOUNDARY + "\"");
            out.print(CRLF);
            out.print("Connection: close");
            out.print(CRLF);
            out.print(CRLF);
            
            // This is the first boundary.  It marks the start of the envelope
            // part of the transport message.
            out.print(CRLF);
            out.print("--" + BOUNDARY);
            out.print(CRLF);
            out.print("Content-Type: application/" + FIPA_XML_ENVELOPE_TYPE);
            out.print(CRLF);
            out.print(CRLF);
            message.printEnvelope(out);
            out.print(Constant.CRLF);
            
            // This is the second boundary.  It marks the end of the envelope,
            // and the start of the payload
            out.print("--" + BOUNDARY);
            out.print(Constant.CRLF);
            out.print(Constant.CRLF);
            out.print(Constant.CONTENT_TYPE + ": application/");
            message.printACL(out);
            out.print(Constant.CRLF);
            out.print(Constant.CRLF);
            message.printPayload(out);
            
            out.print(Constant.CRLF);
            out.print("--" + BOUNDARY + "--");
            out.print(Constant.CRLF);
            out.flush();
            StringTokenizer tok = null;
            String line = null, command = null, code = null;
            
            
            while ((line = in.readLine()) != null) {
                
                if (line.trim().length() == 0) break;
                tok = new StringTokenizer(line);
                command = tok.nextToken();
                if (command.equals(HTTP11)) {
                    code = tok.nextToken();
                }
            }
            
            
            if (!"200".equals(code)) {
                System.err.println("Message correctly received, but not delivered to: " + url + " for: ");
                message.printLogMessage();
                return;
            }
            System.out.println("Message sent to "+host+":"+port);
        } catch (ConnectException ce) {
            System.err.println("(MessageSender) Could not connect to Server on "+url);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }finally{
            try{
                if(in!=null)in.close();
                if(out!=null)out.close();
                if(s!=null)s.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(str[0].equals("mpp"))System.out.println("sending mpp");
        else System.out.println("protocol not supported for "+url);
    }
    
    
}



