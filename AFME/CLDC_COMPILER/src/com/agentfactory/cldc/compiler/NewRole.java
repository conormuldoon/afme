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
 * NewRole.java
 *
 * Created on 09 August 2005, 18:46
 */

package com.agentfactory.cldc.compiler;
import java.util.Collection;

/**
 *
 * @author Conor Muldoon
 */
public class NewRole {
    
    String ident;
    Collection<String>trig;
    Collection<String>r;
    /** Creates a new instance of NewRole */
    public NewRole(String id,Collection<String>rule,Collection<String>trigger) {
        ident=id;
        r=rule;
        trig=trigger;
    }
    
    public void constructTemplate(Collection<String>coll){
        int size=r.size();
        coll.add("        try{");
        coll.add("            TerImplication[]rule=new TerImplication["+size+"];");
        Object[]array=r.toArray();
        for(int i=size;i-->0;){
            coll.add("            rule["+i+"]=new TerImplication(\""+array[i]+"\");");
        }
        size=trig.size();
        coll.add("            FOS[]trigger=new FOS["+size+"];");
        array=trig.toArray();
        for(int i=size;i-->0;){
            coll.add("            trigger["+i+"]=FOS.createFOS(\""+array[i]+"\");");
        }
        coll.add("            FOS id=FOS.createFOS(\""+ident+"\");");
        coll.add("            roleLib.add(id,new RoleTemplate(rule,id,trigger));");
        coll.add("        }catch(MalformedLogicException e){");
        coll.add("            e.printStackTrace();");
        coll.add("        }");
    }
}
