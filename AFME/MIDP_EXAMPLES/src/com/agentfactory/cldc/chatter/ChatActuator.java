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

package com.agentfactory.cldc.chatter;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.AffectManager;
import com.agentfactory.cldc.logic.FOS;

public class ChatActuator extends Actuator {
    
    AffectManager manager;
    /** Creates a new instance of ChatterActuator */
    public ChatActuator(AffectManager manager) {
        super(manager,"chat(?name)");
        this.manager=manager;
    }
    
    public boolean act(FOS action) {
        //System.out.println("chattering");
        manager.actOn(ChatterConstant.CHATTER,ChatterConstant.CHAT,action.next());
        return true;
    }
    
}

