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
 * MessageHolder.java
 *
 * Created on 12 April 2005, 12:01
 */

package com.agentfactory.cldc.mts;

import java.io.DataOutputStream;
import java.io.IOException;

import com.agentfactory.cldc.logic.FOS;

/** The message holder associates message with their recipients. It is used 
 * to store the association until the platform is ready to send the message.
 *
 * @author Conor Muldoon
 */
public class MessageHolder{
    
	Message message;
    FOS to;
    final static byte SIZE=2;
    FOS[]toAddress;
    int size;
    
    /** Creates an instance of MessageHolder.
     * 
     * @param messageTo the agent that the message is to be sent to.
     * @param mess the message that is to be sent.
     * @param toAd the address of the agent that the message is to be sent.
     */
    public MessageHolder(FOS messageTo,Message mess,FOS toAd){
        toAddress=new FOS[SIZE];
        to=messageTo;
        message=mess;
        FOS fos=toAd;

        for(FOS f=fos.next();f!=null;f=fos.next()){
            if(size==toAddress.length){
                FOS[] newArray = new FOS[toAddress.length+SIZE];
                System.arraycopy(toAddress,0,newArray,0,toAddress.length);
                toAddress=newArray;
            }
            toAddress[size]=f;
            size++;
        }

        if(size==0)toAddress[0]=toAd;


    }

    /** Writes the message to the data output stream, along with
     * the name name of the agent that the message is to be sent to.
     * 
     * @param dos the data output stream that the information is to be written to.
     * @throws IOException if there is an I/O error.
     */
    public void write(DataOutputStream dos)throws IOException{

        dos.writeUTF(to.toString());
        dos.writeInt(size);
        for(int i=size;i-->0;)dos.writeUTF(toAddress[i].toString());
        message.write(dos);


    }
}
