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
 * ChatterModule.java
 *
 * Created on 14 February 2005, 19:10
 */

package com.agentfactory.cldc.chatter;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

import com.agentfactory.cldc.AgentName;
import com.agentfactory.cldc.Service;
import com.agentfactory.cldc.UserInterface;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.logic.MalformedLogicException;
/**
 *
 * @author Conor Muldoon
 */
public class ChatterInterface extends Service implements UserInterface,CommandListener{
    TextBox box;
    
    Display display;
    Displayable agt;
    public ChatterInterface(String[]args,Object[]agentName,MIDlet midlet,Displayable displayable){
        super(com.agentfactory.cldc.chatter.ChatterConstant.CHATTER);
        agt=displayable;
        box=new TextBox("Conversation","",200,0);
        if(displayable!=null){
            display=Display.getDisplay(midlet);
            Command agents=new Command("Agents",Command.ITEM,1);
            box.addCommand(agents);
            box.setCommandListener(this);
        }
        
    }
    public void display(){
        display.setCurrent(box);
    }
    public void modifyBinding(Object oldName, Object newName){
        
    }
    public FOS processPer(AgentName name,int perceptionID)throws MalformedLogicException{
        return null;
    }
    public void run(){
        
    }
    public void commandAction(Command command,Displayable dis){
        display.setCurrent(agt);
    }
    
    public FOS processAction(AgentName name,int actionID,FOS data)throws MalformedLogicException{
        StringBuffer buf=new StringBuffer(box.getString());
        buf.append("\nChattering to: ");
        buf.append(data);
        //String string=box.getString()+"\nChattering to: "+data;
        if(buf.length()>200)
            box.setString("Chattering to: "+data.toString());
        else
            box.setString(buf.toString());
        return null;
    }
	
}
