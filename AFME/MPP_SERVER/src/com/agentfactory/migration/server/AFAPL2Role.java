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
 * AFAPL2Role.java
 *
 * Created on 11 August 2005, 18:32
 */

package com.agentfactory.migration.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author Conor Muldoon
 */
public class AFAPL2Role {
    
    Collection<String>rule;
    Collection<String>trigger;
    String id;
    /** Creates a new instance of AFAPL2Role */
    public AFAPL2Role(DataInputStream input) throws IOException{
    	
        rule=new ArrayList<String>();
        for(int i=input.readInt();i-->0;)
            rule.add(input.readUTF());
        
        trigger=new ArrayList<String>();
        for(int i=input.readInt();i-->0;)
            trigger.add(input.readUTF());
        
        id=input.readUTF();
        
       
    }
    
    public void send(DataOutputStream output)throws IOException{
        output.writeInt(rule.size());
        for(String string:rule)output.writeUTF(string);
        output.writeInt(trigger.size());
        for(String string:trigger)output.writeUTF(string);
        output.writeUTF(id);
        
    }
}
