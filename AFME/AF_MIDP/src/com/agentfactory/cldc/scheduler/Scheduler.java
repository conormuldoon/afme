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
 * Scheduler.java
 *
 * Created on 11 February 2005, 16:29
 */

package com.agentfactory.cldc.scheduler;

import java.io.DataOutput;
import java.util.Hashtable;
import java.util.Vector;




/** The scheduler schedules tasks to be executed at periodic intervals.
 * 
 * @author Conor Muldoon
 */
public class Scheduler extends Thread {

	Hashtable taskTable,offTab;

	Vector activeSet;

	boolean active;

	BSTree tree;

	Buffer buffer;

	Rand rand;

	WorkerThread thread[];
	
	Hashtable out;
	
	
	/** Creates a new instance of Scheduler. 
	 * 
	 * @param numThreads the number of worker threads to be created.
	 */
	public Scheduler(int numThreads) {
		thread = new WorkerThread[numThreads];
		
		rand = new Rand();
		active = true;
		taskTable = new Hashtable();
		offTab=new Hashtable();
		activeSet = new Vector();
		buffer = new Buffer();
		tree = new BSTree();
		for (int i = numThreads; i-- > 0;) {
			thread[i] = new WorkerThread(buffer, activeSet);
			thread[i].start();
		}
		out=new Hashtable();

	}

	class TreeNode {
		TreeNode p, left, right;

		ThreadTask task;

		TreeNode(ThreadTask tsk) {
			task = tsk;
		}
	}

	class BSTree {
		TreeNode root;

		void add(TreeNode node) {
			TreeNode y = null;
			TreeNode x = root;
			while (x != null) {
				y = x;
				if (node.task.sooner(x.task))
					x = x.left;
				else
					x = x.right;
			}
			node.p = y;
			if (y == null)
				root = node;
			else if (node.task.sooner(y.task))
				y.left = node;
			else
				y.right = node;
		}

		TreeNode removeTask(TreeNode node, Runnable runnable) {
			if (node == null)
				return null;
			if (node.task.equalsProcess(runnable))
				return remove(node);
			TreeNode n = removeTask(node.right, runnable);
			if (n == null)
				return removeTask(node.left, runnable);
			return n;
		}

		TreeNode remove(TreeNode node) {

			TreeNode y;
			if (node.left == null || node.right == null)
				y = node;
			else
				y = successor(node);
			TreeNode x;
			if (y.left != null)
				x = y.left;
			else
				x = y.right;
			if (x != null)
				x.p = y.p;
			if (y.p == null)
				root = x;
			else if (y == y.p.left)
				y.p.left = x;
			else
				y.p.right = x;
			if (y != node) {
				ThreadTask temp = node.task;
				node.task = y.task;
				y.task = temp;
			}
			y.left = null;
			y.right = null;
			y.p = null;
			return y;

		}

		TreeNode successor(TreeNode node) {
			if (node.right != null)
				return (minimum(node.right));
			TreeNode y = node.p;
			while (y != null && y.right != node) {
				node = y;
				y = y.p;
			}
			return y;
		}

		TreeNode minimum(TreeNode node) {
			while (node.left != null)
				node = node.left;
			return node;
		}
	}

	
	private ExTime exeTime(int period){
		Integer i=new Integer(period);
		ExTime et=(ExTime)offTab.get(i);
		if(et==null){
			et=new ExTime(period,rand.nextInt(period));
			offTab.put(i,et);
		}else et.newAgt();
		return et;
	}
	
	/** Schedules the task identified by the key to be executed periodically.
	 * @param key the identifier for the task.
	 * @param task the task to be executed.
	 * @param period the period at which the task should be executed.
	 */
	public void schedule(Object key, Runnable task, int period) {
		taskTable.put(key, task);
		
		ExTime et=exeTime(period);
		
		TreeNode node = new TreeNode(new ThreadTask(task,et));

		synchronized (this) {
			tree.add(node);
			notify();
		}
	}
	
	/** Reschedules the task identified by the specified key to execute
	 * at period value.
	 * @param key the identifier for the task to be rescheduled.
	 * @param period the period at which the task should execute.
	 */
	public void reschedule(Object key, int period) {
		
		Runnable task = (Runnable) taskTable.get(key);
		ExTime et=exeTime(period);
		
		synchronized (this) {
			TreeNode node = tree.removeTask(tree.root, task);
			node.task.removeTime(offTab);
			node.task = new ThreadTask(task, et);
			tree.add(node);
			notify();
		}

	}
	
	
	/** Schedules a task to be executed on a once off basis.
	 * @param task the task to be executed.
	 */
	public synchronized void schedule(Runnable task) {
		ExTime et=(ExTime)offTab.get(new Integer(-1));
		if(et==null){
			et=new ExTime(-1,0);
		}
		tree.add(new TreeNode(new ThreadTask(task,et)));
		notify();
	}

	

	/** Removes the task represented by the key from the scheduler.
	 * @param key the identifier of the task to be removed.
	 */
	public void remove(Object key) {
		Runnable r = (Runnable) taskTable.remove(key);

		synchronized (this) {
			//node = tree.removeTask(tree.root, task);
			TreeNode node=tree.removeTask(tree.root, r);
			out.put(key,node.task);
			notify();
		}
		

	}
	
	/** Writes the state of the task represented by the key to the data output stream.
	 * This method should only be used where the scheduled task is an agent.
	 * @param key the identifier for the agent task.
	 * @param dos the stream to write the state of the agent to.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	public void writeAgent(Object key,DataOutput dos)throws java.io.IOException{
		ThreadTask task=(ThreadTask)out.get(key);
		task.writeAgent(dos);
	}
	
	/** This method writes the state of the execution time associated with
	 * the task that is represented by the specified key.
	 * @param key the identifier of the task.
	 * @param dos the stream that the state is written to.
	 * @throws java.io.IOException if there is an I/O error.
	 */
	public void writeOut(Object key,java.io.DataOutput dos)throws java.io.IOException{
		ThreadTask task=(ThreadTask)out.get(key);
		task.writeTime(dos);
		task.removeTime(offTab);
		out.remove(key);
	}

	

	/** Pauses the scheduler.
	 * 
	 */
	public void pause() {

		for (int i = thread.length; i-- > 0;)
			thread[i].toggle();
		synchronized (this) {
			if (active) {
				active = false;
				notify();
			} else {
				active = true;
				TreeNode node = tree.minimum(tree.root);
				BSTree newTree = new BSTree();
				
				java.util.Enumeration e=offTab.elements();
				while(e.hasMoreElements())
					((ExTime)e.nextElement()).reSched();
				while (node != null) {
					
					tree.remove(node);
					node.task.schedule();
					newTree.add(node);
					node = tree.minimum(tree.root);
				}
				tree = newTree;
				startThds();
				
			}
		}
	}
	
	private void startThds(){
		for (int i = thread.length; i-- > 0;){
			thread[i].toggle();
			thread[i].start();
		}
		start();
	}
	

	
	/** Destroys the scheduler. This method should be called to free up
	 * resources when the scheduler is no longer needed.
	 * 
	 */
	public synchronized void destroy() {
		active = false;
		notify();

	}
	
	/** Wakes an agent up from a torpor state.
	 * 
	 * @param o an object that represents the name of the agent.
	 */
	public synchronized void wake(Object o){
		((AgentRunnable)taskTable.get(o)).wake();
	}
	
	public void torpor(Object o){
		((AgentRunnable)taskTable.get(o)).torpor();
	}
	
	/** Executes the main control loop of the scheduler. Tasks are periodically
	 * removed from the buffer and executed using the worker threads.
	 * 
	 */
	public void run() throws com.agentfactory.cldc.logic.MalformedLogicException{
		while (active)
			try {
				synchronized (this) {
					if (tree.root == null) {
						wait();
						continue;
					}
					TreeNode node = tree.minimum(tree.root);
					ThreadTask task = node.task;
					long sleepTime = task.calcSleepTime();
					
					
					if (sleepTime > 0) {
						
						wait(sleepTime);
						continue;
					}
					
					node = tree.remove(node);
					if (task.schedule())
						tree.add(node);
					if (!activeSet.contains(task)) {
						activeSet.addElement(task);
						buffer.insert(task);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	
	

}
