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
 * InformFactory.java
 *
 * Created on 11 July 2005, 14:57
 */

package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.Actuator;
import com.agentfactory.cldc.ActuatorFactory;
import com.agentfactory.cldc.AffectManager;
/** Creates an instance of the inform actuator using the specified affect manager.
 *
 * @author Conor Muldoon
 */
public class InformActuatorFact implements ActuatorFactory{

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.ActuatorFactory#createActuator(com.agentfactory.cldc.AffectManager)
	 */
    public Actuator createActuator(AffectManager manager){
        return new InformActuator(manager);
    }

}
