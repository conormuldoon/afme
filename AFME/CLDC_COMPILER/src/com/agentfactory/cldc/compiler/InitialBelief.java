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
 * InitialBelief.java
 *
 * Created on 26 January 2005, 14:34
 */

package com.agentfactory.cldc.compiler;
import java.util.ArrayList;
/**
 *
 * @author Conor Muldoon
 */
public class InitialBelief {
    String agentName;
    String belief;
    /** Creates a new instance of InitialBelief */
    public InitialBelief(String agentName,String belief) {
        this.agentName=agentName;
        this.belief=belief;
    }
    
    public void printAddBelief(ArrayList<String>list){
        list.add("            "+agentName+".addFOSBelief(FOS.createFOS(\""+belief+"\"));");
    }
    
    public boolean equals(Object object){
        InitialBelief bel=(InitialBelief)object;
        return bel.agentName.equals(agentName)&&bel.belief.equals(belief);
    }
}
