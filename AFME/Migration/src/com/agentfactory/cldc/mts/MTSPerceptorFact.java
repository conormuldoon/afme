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
 * MTSFactory.java
 *
 * Created on 11 July 2005, 15:23
 */

package com.agentfactory.cldc.mts;
import com.agentfactory.cldc.PerceptionManager;
import com.agentfactory.cldc.Perceptor;
import com.agentfactory.cldc.PerceptorFactory;
/** Creates an instance of the MTSPerceptor using the specified perception
 * manager. It should be noted that factory classes are only required when migration is being used.
 *
 * @author Conor Muldoon
 */
public class MTSPerceptorFact implements PerceptorFactory{

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.PerceptorFactory#createPerceptor(com.agentfactory.cldc.PerceptionManager)
	 */
    public Perceptor createPerceptor(PerceptionManager manager){
        return new MTSPerceptor(manager);
    }

}
