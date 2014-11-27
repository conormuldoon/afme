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

/* author: Conor Muldoon */
package com.agentfactory.cldc;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.agentfactory.cldc.logic.Agent;
import com.agentfactory.cldc.logic.RoleLibrary;
import com.agentfactory.cldc.logic.TerImplication;
import com.agentfactory.cldc.scheduler.AgentRunnable;

/** This class provides the debugging interface functionality for MIDP
 * environments and enables the developer to view an agent's mental state
 * at run time. It provides functionality to enable the developer to stop an
 * agent, start an agent, and step a cycle of the agent's control process.
 * 
 * 
 * @author Conor Muldoon
 *
 */
public class MIDletRunnable extends AgentRunnable implements CommandListener {

	private StringBuffer beliefBuffer, commitBuffer;

	private MIDlet let;

	private List platformList;

	private String ruleString;

	private long iteration;

	private List list;

	private Object displayLock;

	private TextBox textBox;

	private String currentText;

	private Command back, exit, pause, step, play, next;

	private int numScreens, currentScreen;

	private static final short SIZE = 400;

	private RoleLibrary roleLib;

	private Agent agent;

	private boolean running;
	
	boolean awake;

	/** Creates an instance of MIDletRunnable.
	 * 
	 * @param a the agent the MIDletRunnable displays/controls.
	 * @param midlet the MIDlet for the application.
	 * @param commitmentRule the agent's commitment rules.
	 * @param rl the agent's role library.
	 * @param pList a list of agents on the platform.
	 */
	public MIDletRunnable(Agent a, MIDlet midlet,
			TerImplication[] commitmentRule, RoleLibrary rl, List pList) {
		super(a);
		agent = a;
		running = false;
		awake=true;
		if (midlet != null) {
			displayLock = new int[0];
			roleLib = rl;
			let = midlet;
			platformList = pList;

			beliefBuffer = new StringBuffer();
			commitBuffer = new StringBuffer();
			
			StringBuffer rulesBuffer = new StringBuffer();
			for (int i = commitmentRule.length; i-- > 0;) {
				commitmentRule[i].append(rulesBuffer);
				rulesBuffer.append("\n");
			}
			ruleString = rulesBuffer.toString();
			
			textBox = new TextBox(null, null, SIZE, TextField.ANY);
			textBox.setCommandListener(this);
			
			exit=new Command("Exit",Command.ITEM,4);
			back=new Command("Back",Command.ITEM,4);
			
			textBox.addCommand(exit);
			textBox.addCommand(back);

			play = new Command("Play", Command.ITEM, 3);
			pause = new Command("Pause", Command.ITEM, 3);
			step = new Command("Step", Command.ITEM, 2);
			next = new Command("Next", Command.EXIT, 5);
			textBox.addCommand(play);
			textBox.addCommand(step);
			
		}

	}

	
	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#wake()
	 */
	public void wake(){
		awake=true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#torpor()
	 */
	public void torpor(){
		awake=false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#start()
	 */
	public void start() {
		if (let != null) {
			textBox.addCommand(pause);
			textBox.removeCommand(play);
		}
		running = true;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.agentfactory.cldc.scheduler.AgentRunnable#stop()
	 */
	public void stop() {
		running = false;
	}

	/** Displays three options to the user. View the agent's beliefs,
	 * commitments, or commitment rules.
	 */
	public void display() {

		final String option[] = { "Beliefs", "Commitments", "Commitment Rules" };
		if (list == null) {
			list = new List("Choose Item", List.IMPLICIT, option, null);
			list.setCommandListener(this);
			list.addCommand(exit);
			list.addCommand(back);
		}
		Display.getDisplay(let).setCurrent(list);

	}

	private void removePause() {
		Displayable displayable = Display.getDisplay(let).getCurrent();
		displayable.removeCommand(pause);
		displayable.addCommand(play);
		running = false;

	}

	private void updateBox() {

		String string = null;
		switch (list.getSelectedIndex()) {
		case 0:
			string = beliefBuffer.toString();
			break;
		case 1:
			// fix me: role lib should be in rules screen
			roleLib.append(commitBuffer);
			string = commitBuffer.toString();
			break;
		case 2:
			if (currentText == ruleString)
				return;
			string = ruleString;

		}
		synchronized (displayLock) {

			string = iteration + "\n" + string;

			int length = string.length();
			currentScreen = 0;
			numScreens = length / SIZE;

			if (length % SIZE != 0 || numScreens == 0) {
				numScreens++;

			}
			currentText = string;
			if (length > SIZE) {
				textBox.setString(string.substring(0, SIZE));

				textBox.addCommand(next);

			} else {
				textBox.setString(string);

				textBox.removeCommand(next);

			}

		}

	}

	/** This method handles input coming from the user.
	 *  The user has the option to view an agent's mental state,
	 *  stop the agent, start the agent, pause the agent, step 1 cycle of 
	 *  the agent's control process, or exit the application.
	 *  In the MIDletRunnable class, the user is provided with back
	 *  and next buttons to enable them to navigate between screens. The
	 *  actions of the back and next buttons is also handled by this method.
	 *  
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command c, Displayable d) {
		
		Display display = Display.getDisplay(let);
		
		if (c == List.SELECT_COMMAND) {
			updateBox();
			display.setCurrent(textBox);
		}
		if (c == back) {

			Displayable current = Display.getDisplay(let).getCurrent();
			if (current == textBox) {
				if (currentScreen == 0) {
					display.setCurrent(list);
					return;
				}
				currentScreen--;

				textBox.addCommand(next);
				int startIndex = currentScreen * SIZE;
				synchronized (displayLock) {
					textBox.setString(currentText.substring(startIndex,
							startIndex + SIZE));
				}

			}
			if (current == list) {
				Display.getDisplay(let).setCurrent(platformList);
			}

		} else if (c == pause) {
			removePause();
			return;
		}
		if (c == play) {
			Displayable displayable = display.getCurrent();
			displayable.removeCommand(play);
			displayable.addCommand(pause);
			running = true;
			return;
		}
		if (c == step) {

			removePause();

			synchronized (this) {
				step();
			}
			return;
		}
		if (c == next) {
			currentScreen++;
			int startIndex = currentScreen * SIZE;
			int i = startIndex + SIZE;
			synchronized (displayLock) {
				if (i < currentText.length()) {
					textBox.setString(currentText.substring(startIndex, i));
				} else {
					textBox.setString(currentText.substring(startIndex));
					textBox.removeCommand(next);

				}
			}
			return;
		}
		if (c == exit) {
			let.notifyDestroyed();
		}
	}

	private void step() {

		iteration++;
		beliefBuffer.setLength(0);
		commitBuffer.setLength(0);
		synchronized (displayLock) {
			if(awake)agent.updateBeliefs();
			agent.belBuf(beliefBuffer);
			if(awake)agent.step();
			agent.commitBuf(commitBuffer);
			updateBox();
		}

	}

	/** If the agent is active, updates the agent's beliefs and then
	 * steps one cycle of the control algorithm.
	 */ 
	synchronized public void run() {
		try{
		if (running) {
			if (list == null){
				if(awake){
					agent.updateBeliefs();
					agent.step();
				}
			}else {
				step();
			}
		}
		}catch(Throwable th){
			th.printStackTrace();
		}

	}

}
