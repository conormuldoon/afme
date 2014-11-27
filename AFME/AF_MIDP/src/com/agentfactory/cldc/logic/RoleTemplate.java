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
 * RoleTemplate.java
 *
 * Created on 08 August 2005, 17:26
 */

package com.agentfactory.cldc.logic;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Hashtable;

/** In AFME, support is provided for AFAPL2 roles. A role represents abstract
 * behaviour patterns that agents adopt at various stages of execution.
 * The processing overhead of roles is only incurred when the agents adopt
 * the roles. This improves the efficiency of the reasoning process. Role
 * templates represent schemas that agents can adopt. Variable within
 * the identifier of the role template are applied to create a particular
 * role instance when the role has been triggered or activated. If there
 * are no variables in the identifier, then the role instance
 * is just created directly. See the Agent Factory and AFME documentation for
 * more information.
 * 
 *
 * @author Conor Muldoon
 */
public class RoleTemplate {

    TerImplication[]commitRule;
    
    FOS[]trigger;
    FOS id;

    /** Creates a new instance of RoleTemplate.
     * 
     * @param rule an array of commitment rules.
     * @param identifier a FOS that is used to identify the role.
     * @param trig the triggers for the role.
     */
    public RoleTemplate(TerImplication[]rule,FOS identifier,FOS[]trig) {
        commitRule=rule;
        
        id=identifier;
        trigger=trig;
        
    }
    /** Writes the state of the role template to a data output stream.
     * 
     * @param output the data output stream the state is to be written to.
     * @throws IOException if there is an I/O error.
     */
    public void writeToStream(DataOutput output)throws IOException{
        
        writeArray(commitRule,output);
        writeArray(trigger,output);
        output.writeUTF(id.toString());

    }

    void writeArray(Object[]array,DataOutput output)throws IOException{
        int len=array.length;
        output.writeInt(len);
        for(int i=len;i-->0;)
            output.writeUTF(array[i].toString());
    }

    /** If the specified identifier matches the identifier for the role template,
     * an instance of the role is created provided a role that has the specified
     * identifier does not already exist. The specified hash table represents
     * a mapping between role instances and identifiers. Once a role
     * instance is created by applying the specified identifier to the 
     * template commitment rules, it is added to the hashtable of instances.
     * 
     * @param identifier the identifier of the role instance to be created.
     * @param table the hash table of role instances.
     * @throws MalformedLogicException if there is a logic error.
     */
    public void instantiate(FOS identifier,Hashtable table)throws MalformedLogicException{
        if(id.matches(identifier))
            constructRule(identifier,table);
    }

    void constructRule(FOS identifier,Hashtable table)throws MalformedLogicException{
    	String s=identifier.toString();
        if(!table.containsKey(s)){

            SubstitutionSet binding = new SubstitutionSet(null);
            id.buildSet(identifier, binding);

            int len=commitRule.length;
            TerImplication[]rule=new TerImplication[len];
            for(int i=len;i-->0;)
                rule[i]=commitRule[i].createAppliedRule(binding);
            table.put(s,rule);
            /*
            for(int i=len;i-->0;){
            	StringBuffer sb=new StringBuffer();
            	rule[i].append(sb);
            	System.out.println(sb.toString());
            }
            System.out.println(identifier);*/
        }

    }

    /** Applies the specified substitution set to the identifier for the 
     * role template. If the constructed FOS is not the
     * identifier for a role instance, a role instance is created using the
     * FOS and added to the specified hash table.
     * 
     * @param binding the substitution set that is applied to the template identifier.
     * @param table the hash table of role instances.
     * @throws MalformedLogicException if there is a logic error.
     */
    public void createInstance(SubstitutionSet binding,Hashtable table)throws MalformedLogicException{
        constructRule(id.apply(binding),table);
        
    }

    /** Checks whether the role template has been triggered. If the template
     * has been triggered, a new role instance is created and added to the hash
     * table provided a instance with the same identifier is not currently
     * active.
     * 
     * @param agent the agent that the role template belongs to.
     * @param table the hashtable of role instances.
     * @throws MalformedLogicException if there is a logic error.
     */
    public void processTrigger(Agent agent,Hashtable table)throws MalformedLogicException{
        agent.handleTrigger(trigger,table,this);
    }

}
