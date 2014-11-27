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
 * Rand.java
 *
 * Created on 10 February 2005, 11:26
 */

package com.agentfactory.cldc.scheduler;
import java.util.Random;
/** Rand was created because the standard CLDC Random class does not provide a 
 * {@link #nextInt(int) nextInt(int)} method, which is required in AFME.
 * 
 * @author Conor Muldoon
 */
public class Rand extends Random{
	/** Creates an instance of Rand.
	 * 
	 *
	 */
	public Rand(){
		super();
	}
	
	/** Creates an instance of Rand from the specified seed.
	 * 
	 * @param seed the specified seed.
	 */
	public Rand(long seed){
		super(seed);
	}
	
	/** Returns a pseudorandom number between 0 (inclusive) and the specified number (exclusive).
	 * 
	 * @param n the specified upper limit.
	 * @return the generated pseudorandom number.
	 */
    public int nextInt(int n){
        if((n&-n)==n)return(int)((n*(long)next(31))>>31);
        int bits,val;
        do {
            bits=next(31);
            val=bits%n;
        }while(bits-val+(n-1)<0);
        return val;
    }

}
