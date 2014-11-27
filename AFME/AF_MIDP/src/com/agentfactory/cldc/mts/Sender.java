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
 * Sender.java
 *
 * Created on 12 September 2005, 13:41
 */

package com.agentfactory.cldc.mts;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.agentfactory.cldc.logic.FOS;

/** Sends a message directly within its own socket to a destination address.
 * This class is used by the inform actuator and request actuator when the
 * message transport service is operating in asynchronous mode.
 *
 * @author Conor Muldoon
 */
public class Sender {

	/** Sends a message within a socket.
	 * 
	 * @param fos a FOS representation of the URL of the destination
	 * of the message.
	 * @param perf the performative of the message.
	 * @return true if the message sent correctly, false if there was an exception.
	 */
    public static boolean send(FOS fos,String perf){
       // System.out.println("sending "+fos);
    	try{
        	
            StreamConnection conn = (StreamConnection) Connector.open(fos.next().toString());
            
            DataOutputStream dos=conn.openDataOutputStream();
            dos.writeUTF(ServerConstant.DIRECT_MESSAGE);

            dos.writeUTF(fos.next().toString());
            dos.writeUTF(fos.next().toString());
            dos.writeUTF(perf);
            dos.writeUTF(fos.next().toString());
            dos.writeUTF(fos.next().toString());
            conn.close();
            dos.close();
        }catch(ConnectionNotFoundException cnfe){
            cnfe.printStackTrace();
            return false;
        }catch( IOException e ){
            e.printStackTrace();
            return false;
        }
        //System.out.println("message sent");
        return true;
    }

}
