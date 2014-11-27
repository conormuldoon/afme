/**
 * Title:       MalformedLogicException.java
 * Copyright:   Copyright (c) 1996-2004 The Agent Factory Working Group. All rights reserved.
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
 *              along with Agent Factory; see the file COPYING.  If not, write to
 *              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *              Boston, MA 02111-1307, USA. 
 */

package com.agentfactory.cldc.logic;

/** This exception is thrown when there is a logic problem.
 * 
 *
 */
public class MalformedLogicException extends RuntimeException {
	/** Creates an instance of MalformedLogicExcpetion.
	 * 
	 * @param inString the message that is displayed by the exception.
	 */
	public MalformedLogicException(String inString) {
		super("Logic Problem: " + inString);
	}
}

