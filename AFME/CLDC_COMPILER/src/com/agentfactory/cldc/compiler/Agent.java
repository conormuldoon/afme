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
 * Agent.java
 *
 * Created on 26 January 2005, 15:49
 */

package com.agentfactory.cldc.compiler;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Conor Muldoon
 */
public class Agent {
    String name;
    Role role;
    String sleepTime;
    /** Creates a new instance of Agent */
    public Agent(String name,Role role,String st) {
        this.name=name;
        this.role=role;
        sleepTime=st;
    }
    
    public void start(Collection<String> coll,String string){
        if(string.equals(name)){
            coll.add("        "+name+".start();");
            
        }
       
    }
    
    public void addName(StringBuffer buffer){
        buffer.append(name);
    }
    public void addSleepTime(StringBuffer buffer){
        buffer.append(sleepTime);
    }
    public void addDisplay(ArrayList<String>list){
        list.add("//--                "+name+".display();");
    }
    
    public void printConstruction(ArrayList<String>list){
        list.add(role.addConstructName("        "+name+"=")+"("+name+"Name);");
        
        list.add("         "+name+"Name.addToTable(agents,"+name+");");
    }
    
   
    
    public boolean equals(Object o){
        return ((Agent)o).name.equals(name);
    }
    
    
}
