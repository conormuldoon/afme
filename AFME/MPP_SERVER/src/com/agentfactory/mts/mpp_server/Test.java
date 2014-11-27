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
 * Test.java
 *
 * Created on 04 March 2005, 14:07
 */

package com.agentfactory.mts.mpp_server;
import java.net.*;
/**
 *
 * @author Conor Muldoon
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    public static void main(String[]args){
        try{
            InetAddress address=InetAddress.getLocalHost();
            System.out.println(address.toString());
            System.out.println(address.getHostAddress());
            System.out.println(address.getHostName());
            System.out.println(address.getCanonicalHostName());
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
