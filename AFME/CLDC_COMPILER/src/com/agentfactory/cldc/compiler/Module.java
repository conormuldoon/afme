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
 * Module.java
 *
 * Created on 26 January 2005, 17:37
 */

package com.agentfactory.cldc.compiler;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Conor Muldoon
 */
public class Module {
    
    String className;
    Map<String,String>modMap;
    String name;
    /** Creates a new instance of Module */
    public Module(String nm,String className,Map<String,String>mm) {
        name=nm;
        modMap=mm;
        this.className=className;
    }
    
    public void register(List<String>list){
    	String s=modMap.get(name);
    	String cn=(s==null)?className:s;
        if(name.equals("interface")){
            list.add("        "+cn+" interMod=new "+cn+"(name,let,list,scheduler);");
            list.add("        interMod.register(modules);");
            list.add("        inter=interMod;");
        }
        else list.add("        new "+cn+"(name).register(modules);");
    	
    }
    
    public boolean equals(Object object){
        Module mod=(Module)object;
        String s=modMap.get(name);
        String cn=(s==null)?className:s;
        s=modMap.get(mod.name);
        String cn2=(s==null)?mod.className:s;
        return cn.equals(cn2);
    }
    
}
