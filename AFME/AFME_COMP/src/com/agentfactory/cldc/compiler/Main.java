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
package com.agentfactory.cldc.compiler;

import antlr.CommonAST;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import java.io.*;
/** This is the main compiler program.
 * 
 * @author Conor Muldoon
 *
 */
public class Main {

	/** Executes the compiler on the specified file name. If there are one or two arguments, the first argument (args[0]) is the file name.
	 * If there are three arguments, the second argument (args[1]) is the classpath, the third argument is the file name, and the first argument is the classpath flag.
	 * If the final argument is the -p flag, prime numbers will be generated for the response times.
	 * 
	 * @param args the arguments to the program.
	 */
	public static void main(String[] args) {
		// ARGS: -p generate prime numbers for response times of agents
		
		String fileName=args[0];
		String classpath=null;
		boolean p=false;
		if(args.length==2){
			if(args[1].equals("-p")){
				p=true;
			}
		}else if(args.length==3){
			classpath=args[1];
			fileName=args[2];
				
		}else if(args.length==4){
			if(args[1].equals("-p")){
				p=true;
				classpath=args[2];
				fileName=args[3];
			}else {
				classpath=args[1];
				fileName=args[2];
				if(args[3].equals("-p"))p=true;
			}
		}
			
		
		System.out.println("Processing: " + fileName);
		URLClassLoader loader = null;
		InputStream is=null;
		try {
			if (classpath != null) {
				StringTokenizer tok = new StringTokenizer(classpath, ";");
				int numToks = tok.countTokens();

				URL[] urls = new URL[numToks];
				for (int i = numToks; --i >= 0;) {
					String string = tok.nextToken();
					urls[i] = new File(string).toURL();
					
				}
				is = (loader = new URLClassLoader(urls))
						.getResourceAsStream(fileName);
			}
			if(is==null){
				is = new FileInputStream(new File(fileName));
			}
	
			
				
				AFMELexer lex = new AFMELexer(is);
				
				AFMEParser parse = new AFMEParser(lex);
				parse.begin();
				
				CommonAST t = (CommonAST) parse.getAST();
				AFMEWalker walker = new AFMEWalker();
				
				walker.begin(t,loader,p);
				System.out.println("Completed: " + fileName);

			

		} catch (FileNotFoundException e) {
			System.out.println("Error file not found: " + fileName);
		} catch (IOException e) {
			System.out.println("Error processing file: " + fileName);
		}catch(TokenStreamException e) {
		      System.err.println("Problem with stream: "+e);
	    }
	    catch(RecognitionException re) {
	      System.err.println("Bad input: "+re);
	    }
	}

}
