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
 * MigActuator.java
 *
 * Created on 27 June 2005, 18:15
 */

package com.agentfactory.migration.server;

import com.agentfactory.platform.logic.FOS;
import com.agentfactory.platform.interfaces.Actuator;

import com.agentfactory.plugins.interpreters.afapl2.AFAPLAgent;

/**
 * 
 * @author Conor Muldoon
 */
public class MigActuator extends Actuator {

	/** Creates a new instance of MigActuator */
	public MigActuator() {
		super("migrate(?platform,?host,?port,?url)");
	}

	public boolean act(FOS action) {
		MigrationService service = (MigrationService) agent
				.getService("cldc.migration");
		AFAPLAgent agt = (AFAPLAgent) agent;
		OutgoingAgent outAgt = new OutgoingAgent(action.argAt(0).toString(),
				action.argAt(1).toString(), Integer.parseInt(action.argAt(2)
						.toString()), action.argAt(3).toString(), agt);
		service.addOutgoing(agent.getName(), outAgt);
		try {
			MigrationController migcon = (MigrationController) agt
					.getController();
			migcon.migrate("emigration " + action);
		} catch (ClassCastException e) {
			System.out.println("Agent does not contain the correct controller.");
			System.out.println("com.agentfactory.migration.server.MigrationController required");
			System.out.println("Cannot migrate");
			retractBelief("ALWAYS(BELIEF(onCLDC))");
		}

		return true;
	}

}
