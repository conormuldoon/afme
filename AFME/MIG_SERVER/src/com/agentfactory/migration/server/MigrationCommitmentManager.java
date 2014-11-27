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
 * MigrationCommitmentManager.java
 *
 * Created on 11 August 2005, 17:16
 */

package com.agentfactory.migration.server;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.agentfactory.plugins.interpreters.afapl2.control.BasicCommitmentManager;
import com.agentfactory.plugins.interpreters.afapl2.mentalOject.CommitmentRule;
/**
 *
 * @author Conor Muldoon
 */

public class MigrationCommitmentManager extends BasicCommitmentManager{
    
    Collection<String>commitment;
    
    /** Creates a new instance of MigrationCommitmentManager */
    public MigrationCommitmentManager() {
        super();
        commitment=new ArrayList<String>();
    }
    public void addCommitmentRule(CommitmentRule commitmentRule){
        
        super.addCommitmentRule(commitmentRule);
        commitment.add(commitmentRule.toString());
    }
    public void writeCommitment(DataOutputStream out)throws IOException{
        out.writeInt(commitment.size());
        for(String terImp:commitment)
            out.writeUTF(terImp);
    }
}
