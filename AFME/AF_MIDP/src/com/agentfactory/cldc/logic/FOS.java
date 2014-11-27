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

package com.agentfactory.cldc.logic;

import java.util.Hashtable;

/**
 * The FOS class represents one of the primary logical components of Agent
 * Factory/AFME. It provides an abstract or symbolic representation of the
 * information content.
 * 
 * @author Conor Muldoon
 * 
 */
public class FOS {
	static final char COMMA = ',';
	public static final char OPEN_BRACKET = '(';
	public static final char CLOSE_BRACKET = ')';
	static final byte SIZE = 4;

	static Hashtable variables = new Hashtable();

	String functor;
	FOS[] arguments;
	int tail;
	int cur;

	// public void printState(){
	// System.out.println(tail+" "+cur+" "+arguments.length+" #"+functor+'#');
	// }
	/**
	 * Creates a FOS from the specified string representation if the string does
	 * not represent a variable. If the string does represent a variable and the
	 * variable was previously created, returns the previously created FOS. If
	 * the variable was not previously created, returns a new FOS for the
	 * variable.
	 * 
	 * @param string
	 *            the specified string representation.
	 * @return either a variable or composite FOS.
	 * @throws MalformedLogicException
	 *             if there is a logic error.
	 */
	public static FOS createFOS(String string) throws MalformedLogicException {
		if (variables.containsKey(string))
			return (FOS) variables.get(string);
		FOS fos = new FOS(string);
		if (fos.isVariable())
			variables.put(string, fos);
		return fos;
	}

	private FOS(String functor, FOS[] fos, int t) {
		this.functor = functor;
		this.arguments = fos;
		tail = t;
	}

	private FOS(String string) throws MalformedLogicException {
		arguments = new FOS[SIZE];
		int startIndex = string.indexOf(OPEN_BRACKET);
		if (startIndex == -1)
			functor = string.trim();
		else {
			functor = string.substring(0, startIndex).trim();
			int endIndex = string.lastIndexOf(CLOSE_BRACKET);
			if (endIndex == -1)
				throw new MalformedLogicException("Failed to build: " + string);
			string = string.substring(startIndex + 1, endIndex).trim();
			int bracketCount = 0;
			char character;
			int index = 0;
			for (int i = 0; i < string.length(); i++) {
				character = string.charAt(i);
				if (character == COMMA && bracketCount == 0) {
					add(createFOS(string.substring(index, i)));
					index = i + 1;
				} else {
					if (character == OPEN_BRACKET)
						bracketCount++;
					else if (character == CLOSE_BRACKET)
						bracketCount--;
				}
			}
			add(createFOS(string.substring(index).trim()));
		}
	}

	/**
	 * Returns an inverted FOS. An inverted FOS is one in
	 * which the truth value has been toggle. In AFME, a FOS that is preceded
	 * with the ! symbol is negated. ! represent not. So for instance, !a
	 * represent not a. If !a was passed to this method, a would be returned. If
	 * a was passed to this method, !a would be returned.
	 * 
	 * @return an inverted FOS.
	 */
	public FOS invert() {
		if (isNot())
			return new FOS(functor.substring(1), arguments, tail);
		return new FOS('!' + functor, arguments, tail);
	}

	/**
	 * Adds the specified FOS to the arguments of this FOS.
	 * 
	 * @param fos
	 *            the specified FOS.
	 */
	void add(FOS fos) {
		if (tail == arguments.length - 1) {
			FOS[] newArray = new FOS[arguments.length << 1];
			System.arraycopy(arguments, 0, newArray, 0, tail);
			arguments = newArray;
		}
		arguments[tail] = fos;
		tail++;
	}

	/**
	 * Tests whether this is an inverted FOS.
	 * 
	 * @return true if this is an inverted FOS, false otherwise.
	 */
	public boolean isNot() {
		return functor.charAt(0) == '!';
	}

	/**
	 * Tests if the FOS has more arguments.
	 * 
	 * @return true if the FOS has more arguments, false otherwise.
	 */
	public boolean hasNext() {
		return cur < tail;
	}

	/**
	 * Returns the next argument in the FOS and increments the internal counter,
	 * which is used to determine the current argument. Once all of the
	 * arguments have been return, a null argument is returned the first time
	 * the next() method is subsequently called. A sentinel is used so as that
	 * an if statement is not required to check for the last element and to
	 * thereby improve efficiency.
	 * 
	 * @return the next argument in the FOS.
	 */
	public FOS next() {

		FOS fos = arguments[cur];

		cur++;
		return fos;

	}

	/**
	 * Resets the FOS to the state in which the next method has never been
	 * called. The reset method is recursively called on each of the FOS's
	 * children.
	 * 
	 */
	public void reset() {
		cur = 0;
		for (int i = tail; i-- > 0;)
			arguments[i].reset();
	}

	/**
	 * Checks whether the FOS contains the specified character.
	 * 
	 * @param c
	 *            the character to check for.
	 * @return true if the FOS contains the specified character, false
	 *         otherwise.
	 */
	public boolean containsChar(char c) {
		if (functor.indexOf(c) > -1)
			return true;
		for (int i = tail; i-- > 0;)
			if (arguments[i].containsChar(c))
				return true;
		return false;
	}

	/**
	 * Tests whether the functor equals the specified object.
	 * 
	 * @param object
	 *            the object to compare the functor to.
	 * @return true if the functor is equal to the specified object, false
	 *         otherwise.
	 */
	public boolean functorEquals(Object object) {
		if (functor == object)
			return true;
		return functor.equals(object);
	}

	/**
	 * Removes the object from the hash table whose key is the functor of this
	 * FOS.
	 * 
	 * @param table
	 *            the hash table to remove the object from.
	 * @return the object from the hash table whose key is equal to the functor
	 *         of this FOS, null if no such object in the table.
	 */
	public Object removeFromTable(java.util.Hashtable table) {
		return table.remove(functor);
	}

	/**
	 * Adds the specified object to the hash table using the functor of the FOS
	 * as the key.
	 * 
	 * @param table
	 *            the table the object is to be added to.
	 * @param object
	 *            the object to add to the table.
	 */
	public void addToTable(Hashtable table, Object object) {
		table.put(functor, object);
	}

	/**
	 * Obtains the object from the hash table whose key is the functor of the
	 * FOS.
	 * 
	 * @param table
	 *            the hash table to obtain the object from.
	 * @return an object from the hashtable whose key is the functor of the FOS,
	 *         null if there is no such object.
	 */
	public Object getFromTable(Hashtable table) {
		return table.get(functor);
	}

	/**
	 * Tests whether this FOS is a variable. In AFME, variables are preceded
	 * with the ? symbol.
	 * 
	 * @return true if this FOS is a variable, false otherwise.
	 */
	private boolean isVariable() {
		return tail == 0 && functor.charAt(0) == '?';
	}

	/**
	 * Test whether this is a true FOS. A FOS is true in AFME
	 * if it has no arguments and its functor is equal to the string true. A
	 * true FOS has a special meaning in AFME. If true is encoded in a belief
	 * sentence or commitment, the agent need not adopt a belief true for the
	 * commitment to hold or the belief sentence to be evaluated to true. It is
	 * taken that under all circumstance the evaluation will be true.
	 * 
	 * @return true if this FOS is true, false otherwise.
	 */
	public boolean isTrue() {
		return tail == 0 && functor.equals("true");

	}

	/**
	 * Tests whether this FOS matches the specified FOS. A matching is defined
	 * recursively as the following: Two FOSs match if (1) either are variables
	 * or (2) they have the same functor, same number (n) of arguments, and each
	 * argument a(i) of the specified FOS matches each argument b(i) of this FOS
	 * where i = 0 to n-1 and arguments are 0 indexed. So for example,
	 * hello(Alice) and hello(Bob) would both match with hello(?x) but they
	 * would not match with each other. It should be noted that
	 * hello(Alice(inChains)) would also match with hello(?x). This is because
	 * ?x would match with Alice(inChains) since ?x is a variable.
	 * 
	 * @param f
	 *            the specified FOS.
	 * @return true if the FOSs match, false otherwise.
	 */
	public boolean matches(FOS f) {
		if (f.isVariable() || isVariable())
			return true;
		if (!(f.functor.equals(functor) && tail == f.tail))
			return false;
		for (int i = tail; i-- > 0;)
			if (!arguments[i].matches(f.arguments[i]))
				return false;
		return true;
	}

	/**
	 * Applies the substitution set to the arguments of this FOS and to the
	 * arguments of the specified FOS. For each ordinal pair of arguments, if
	 * one of the arguments are variables, a substitution is added to the set
	 * linking the two arguments. If neither arguments are variables, the
	 * buildSet method is called recursively.
	 * 
	 * @param fos
	 *            the FOS used to build the set mappings.
	 * @param solution
	 *            the substitution set being built.
	 */
	public void buildSet(FOS fos, SubstitutionSet solution) {
		for (int i = 0; i < tail; i++) {
			FOS arg = arguments[i].apply(solution);
			FOS fosArg = fos.arguments[i].apply(solution);
			if (arg.isVariable()) {
				if (!fosArg.containsVariable(arg))
					solution.add(arg, fosArg);
			} else if (fosArg.isVariable()) {
				if (!arg.containsVariable(fosArg))
					solution.add(fosArg, arg);
			} else
				arg.buildSet(fosArg, solution);
		}
	}

	private boolean containsVariable(FOS f) {
		if (this == f)
			return true;
		for (int i = tail; i-- > 0;)
			if (arguments[i].containsVariable(f))
				return true;
		return false;
	}

	/**
	 * Applies the specified substitution set to the FOS.
	 * 
	 * @param sub
	 *            the specified substitution set.
	 * @return a FOS that has had the substitution set applied to it.
	 */
	public FOS apply(SubstitutionSet sub) {

		if (isVariable())
			return sub.replaceVar(this);
		if (tail == 0)
			return this;
		FOS[] fos = new FOS[tail + 1];
		boolean b = true;
		for (int i = 0; i < tail; i++) {
			fos[i] = arguments[i].apply(sub);
			if (fos[i] != arguments[i])
				b = false;
		}
		if (b)
			return this;
		return new FOS(functor, fos, tail);
	}

	/**
	 * Tests whether the FOS represents an equality or inequality expression.
	 * 
	 * @return true if the FOS is an expression, false otherwise.
	 */
	public boolean isExpression() {
		return functor.charAt(0) == '#';
	}

	/**
	 * Evaluates FOSs that represent expressions.
	 * 
	 * @return true if the expression does hold, false otherwise.
	 */
	public boolean eval() {
		char sym = functor.charAt(1);
		int n = functor.charAt(2)-'0';
		int a = 0, b = 0;
		for (int i = n; i-- > 0;) {
			
			if (functor.charAt(3 + i) == '+')
				a += Integer.parseInt(arguments[i].toString());
			else
				a -= Integer.parseInt(arguments[i].toString());
		}
		for (int i = tail; i-- > n;){
			
			if (functor.charAt(3 + i) == '+')
				b += Integer.parseInt(arguments[i].toString());
			else
				b -= Integer.parseInt(arguments[i].toString());
		}

		//System.out.println(a+""+sym+b);
		switch (sym) {
		case '>':
			return a > b;
		case '<':
			return a < b;
		case '=':
			return a == b;
		case 'g':
			return a >= b;
		default:
			return a <= b;
		}
	}

	/**
	 * Appends the FOS to the specified string buffer.
	 * 
	 * @param buffer
	 *            the string buffer that the FOS is appended to.
	 */
	public void append(StringBuffer buffer) {
		buffer.append(functor);
		if (tail == 0)
			return;
		buffer.append('(');
		arguments[0].append(buffer);
		for (int i = 1; i < tail; i++) {
			buffer.append(',');
			arguments[i].append(buffer);
		}
		buffer.append(')');
	}

	/**
	 * Returns the hash code for the FOS.
	 * 
	 */
	public int hashCode() {
		// extend this for arguments
		return functor.hashCode();
	}

	/**
	 * converts this FOS to a string.
	 * 
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		append(buffer);
		return buffer.toString();
	}

	/**
	 * Checks whether the functor of this FOS starts with the specified string.
	 * 
	 * @param string
	 *            the specified string.
	 * @return true if the specified string is a prefix of the functor, false
	 *         otherwise.
	 */
	public boolean functorStartsWith(String string) {
		return functor.startsWith(string);
	}

	/**
	 * Checks whether this FOS is equal to the specified FOS.
	 * 
	 * @param fos
	 *            the specified FOS.
	 * @return true if this FOS is equal to the specified FOS, false otherwise.
	 */
	public boolean equals(FOS fos) {
		if (this == fos)
			return true;
		if (!(fos.functor.equals(functor) && tail == fos.tail))
			return false;
		for (int i = tail; i-- > 0;)
			if (!arguments[i].equals(fos.arguments[i]))
				return false;
		return true;
	}
}
