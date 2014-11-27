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
 
act com.agentfactory.cldc.AdoptActuator;
act com.agentfactory.cldc.RetractActuator;
act com.agentfactory.cldc.mts.UpdateAIDActuator;
act com.agentfactory.cldc.mts.AddAgentIDActuator;
act com.agentfactory.cldc.mts.InformActuator;
act com.agentfactory.cldc.mts.RequestActuator;
act com.agentfactory.cldc.EnactRoleActuator;
act com.agentfactory.cldc.DeactivateRoleActuator;
per com.agentfactory.cldc.SelfPerceptor;
per com.agentfactory.cldc.mts.MTSPerceptor;


// Advertise in Yellow Pages
providingService(?service) , fipaAgent(?agt) >
request(?agt,advertise(?service));

// Make a yellow pages request
yellowPageAgent(?service) , fipaAgent(?agt) >
request(?agt,yellowPages(?service));

register , fipaAgent(?agt)>
request(?agt,register);

// Make a white pages request
AIDRequired(?agent) , fipaAgent(?agt) > 
request(?agt,whitePages(?agent));

// Add agent id
newAgentID(?name,?addresses)
>
addAgentID(?name,?addresses);

// Add agent id of acquaintance
message(?perf,sender(?agt,?addresses),?content),
!agentID(?agt,?addresses)>
addAgentID(?agt,?addresses);

message(inform,?sender,agentID(?agent, ?addresses)) ,
!agentID(?agent, ?addresses) >
addAgentID(?agent, ?addresses);

//message(inform, ?sender, changedAID(?oldName, ?name)) > updateAID(?oldName, ?name);

