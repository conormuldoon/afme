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
 * GraphicalInterface.java
 *
 * Created on 24 March 2005, 15:12
 */

package com.agentfactory.cldc.compiler;

/**
 *
 * @author Conor Muldoon
 */
public class GraphicalInterface {
    
    Object name;
    String period;
    String []args;
    /** Creates a new instance of Service */
    public GraphicalInterface(Object nm,String prd,String[]tok,int val) {
        name=nm;
        period=prd;
        int num=tok.length-val;
        if(num>0){
            args=new String[num];
            
            for(int i=0;i<num;i++){
                args[i]=tok[num+val];
            }
        }
    }

    public void addConstruction(java.util.Collection<String>coll){
        if(args!=null){
            coll.add("        args=new String["+args.length+"];");
            for(int i=0;i<args.length;i++){
                coll.add("        args["+i+"]=\""+args[i]+"\";");
            }
        }
        //coll.add("        Display display=Display.getDisplay(let);");
        coll.add("        "+name+" interService = new "+name+"(args,agentName,let,list);");
        coll.add("        interService.register(services);");
        coll.add("        inter=interService;");
        if(!period.equals("0")){
            coll.add("        inter.register(services,scheduler,"+period+");");
        }
    }
    
    
}