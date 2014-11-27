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
 * Module.java
 *
 * Created on 28 June 2005, 11:47
 */

package com.agentfactory.migration.server;
import java.io.IOException;
import java.io.DataOutputStream;
/**
 *
 * @author Conor Muldoon
 */
public class Module {
    String name;
    String cls;
    /** Creates a new instance of Module */
    public Module(String n,String c) {
        
        name=n;
        cls=c;
    }
    
    public void send(DataOutputStream dos)throws IOException{
        
        dos.writeUTF("LOAD_MODULE "+name+" "+cls);
        
    }
    
}
