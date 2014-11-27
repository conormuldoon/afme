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

package com.agentfactory.cldc;
import com.agentfactory.cldc.logic.FOS;


/** The adopt actuator is used when an agent wishes to adopt a belief. The belief will
 * be present on the subsequent iteration of the control algorithm. The trigger
 * for the actuator is adoptBelief(?belief). The ?belief variable represents
 * the belief to be adopted.
 * @author Conor Muldoon
 *
 */
public class AdoptActuator extends Actuator {

  /** Constructs an AdoptActuator using the specified manager.
   * The manager is passed to the super Actuator constructor along with the
   * FOS identifier for the AdoptActuator. The identifier is
   * adoptBelief(?belief)
   * 
   * @param manager the specified manager.
   */
    public AdoptActuator(AffectManager manager) {
        super(manager,"adoptBelief(?belief)");
    }

    /*
     *  (non-Javadoc)
     * @see com.agentfactory.cldc.logic.Action#act(com.agentfactory.cldc.logic.FOS)
     */
    public boolean act(FOS action) {

        adoptBelief(action.next());
        return true;
    }
}

