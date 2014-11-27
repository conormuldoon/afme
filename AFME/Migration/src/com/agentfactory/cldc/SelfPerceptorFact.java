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
 * SelfFactory.java
 *
 * Created on 11 July 2005, 15:21
 */



package com.agentfactory.cldc;
/** Creates an instance of the self perceptor using the perception manager
 * for the agent. It should be noted that factories are only required when migration
 * is being used.
 *
 * @author Conor Muldoon
 */
public class SelfPerceptorFact implements PerceptorFactory{

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.PerceptorFactory#createPerceptor(com.agentfactory.cldc.PerceptionManager)
	 */
    public Perceptor createPerceptor(PerceptionManager manager){
        return new SelfPerceptor(manager);
    }

}
