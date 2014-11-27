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
 
 /**
 * Filename: UniqueAgent.sh
 * Author:   Conor Muldoon
 * Date:     14 April 2005
 */

// This type of agent differs from a platform specific
// agent in agent factory standard edition in that
// a unique agent may migrate and thus could no longer
// be associated with a platform.

act com.agentfactory.cldc.AdoptActuator;
act com.agentfactory.cldc.RetractActuator;
act com.agentfactory.cldc.mts.UpdateAIDActuator;
act com.agentfactory.cldc.mts.AddAgentIDActuator;
act com.agentfactory.cldc.mts.InformActuator;
act com.agentfactory.cldc.mts.RequestActuator;
act com.agentfactory.cldc.name_service.LocalNameActuator;
act com.agentfactory.cldc.name_service.UniqueNameActuator;
act com.agentfactory.cldc.name_service.UpdateNameActuator;
act com.agentfactory.cldc.name_service.RegisterAgentActuator;
act com.agentfactory.cldc.EnactRoleActuator;
act com.agentfactory.cldc.DeactivateRoleActuator;
act com.agentfactory.cldc.PrintActuator;
per com.agentfactory.cldc.SelfPerceptor;
per com.agentfactory.cldc.mts.MTSPerceptor;


// Advertise in Yellow Pages
providingService(?service) , !nameCreate ,
fipaAgent(?agt)>
request(?agt,advertise(?service));

providingService(?service) , nameCreate>
adoptBelief(providingService(?service));

// Make a yellow pages request
yellowPageLookup(?service) , !nameCreate ,
fipaAgent(?agt)>
request(?agt,yellowPages(?service));

yellowPage(?service) , nameCreate>
adoptBelief(yellowPageLookUp(?service));

// Register for unique name
nameCreate , !created>
par(adoptBelief(always(created)),
uniqueName);

requestName , fipaAgent(?agent)>
request(?agent,uniqueName);

haveName , fipaAgent(?agent)>
par(request(?agent,register),
retractBelief(always(nameCreate)));

message(?perf,?sender,uniqueName(?uniqueValue)) ,nameCreate >
seq(updateName(?uniqueValue),registerAgent);

// Make a white pages request
AIDRequired(?agent), !nameCreate,
fipaAgent(?agt)> 
request(?agt,whitePages(?agent));

AIDRequired(?agent), nameCreate>
adoptBelief(AIDRequired(?agent));

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