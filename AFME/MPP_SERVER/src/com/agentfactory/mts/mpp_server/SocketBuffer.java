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
 * SocketBuffer.java
 *
 * Created on 08 February 2005, 18:42
 */

package com.agentfactory.mts.mpp_server;
import java.util.*;
import java.net.Socket;
/**
 *
 * @author Conor Muldoon
 */
public class SocketBuffer {
    Queue<Socket>list;
    /** Creates a new instance of SocketBuffer */
    public SocketBuffer(){
        list=new LinkedList<Socket>();
    }
    
    public synchronized void insert(Socket socket){
        list.add(socket);
        notify();
    }
    
    public synchronized Socket extract()throws InterruptedException{
        // Spin Lock
        while(list.size()==0)
            wait();
        return list.remove();
        
    }
    
}

