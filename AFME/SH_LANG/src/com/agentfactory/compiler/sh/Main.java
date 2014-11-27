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
package com.agentfactory.compiler.sh;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import antlr.CommonASTWithHiddenTokens;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamHiddenTokenFilter;

/** This is the main program for the cross compiler.
 * 
 * @author Conor Muldoon
 *
 */
public class Main {
	public static TokenStreamHiddenTokenFilter filter;
	static final String AF="afapl2";
	static final String SH="sh";
	
	/**
	 * 
	 * @param args the arguments to the program. The first argument represents the file name.
	 */
	public static void main(String[] args) {

		System.out.println("Processing: " + args[0]);
		try {

			if (args[0].endsWith(SH)) {
				File file=new File(args[0]);
				SHCompLexer lex = new SHCompLexer(new FileInputStream(file));
				
				lex.setTokenObjectClass("antlr.CommonHiddenStreamToken");
				filter = new TokenStreamHiddenTokenFilter(
						lex);
				filter.hide(SHCompParser.WS);
				filter.hide(SHCompParser.SL_COMMENT);
				filter.hide(SHCompParser.ML_COMMENT);

				SHCompParser parse = new SHCompParser(filter);
				parse.setASTNodeClass("antlr.CommonASTWithHiddenTokens");

				parse.shapl();
				
				CommonASTWithHiddenTokens t =
				    (CommonASTWithHiddenTokens)parse.getAST();

				SHWalker walker = new SHWalker();
				walker.createFile(args[0].substring(0,args[0].indexOf(".")+1)+AF);
				try {
					walker.walk(t);
				} catch (antlr.RecognitionException e) {
					System.err.println("exception: " + e);
				}
				walker.close();
				System.out.println("Completed: " + args[0]);
			} else if (args[0].endsWith(AF)) {
				File file=new File(args[0]);
				AFAPLLexer lex = new AFAPLLexer(new FileInputStream(file));
				lex.setTokenObjectClass("antlr.CommonHiddenStreamToken");
				filter = new TokenStreamHiddenTokenFilter(
						lex);
				filter.hide(AFAPLParser.WS);
				filter.hide(AFAPLParser.SL_COMMENT);
				filter.hide(AFAPLParser.ML_COMMENT);

				AFAPLParser parse = new AFAPLParser(filter);
				parse.setASTNodeClass("antlr.CommonASTWithHiddenTokens");

				parse.reverse();
				
				CommonASTWithHiddenTokens t =
				    (CommonASTWithHiddenTokens)parse.getAST();
				
				RevWalker walker = new RevWalker();
				walker.createFile(args[0].substring(0,args[0].indexOf(".")+1)+SH);
				try {
					walker.walk(t);
				} catch (antlr.RecognitionException e) {
					System.err.println("exception: " + e);
				}
				walker.close();
				
				System.out.println("Completed: " + args[0]);
			} else {
				System.out.println("Unrecognised extension: " + args[0]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file: " + args[0]);
		}catch(IOException e){
			System.out.println("Problem processing file: " + args[0]);
		}
		catch(TokenStreamException e) {
		      System.err.println("problem with stream: "+e);
	    }
	    catch(RecognitionException re) {
	      System.err.println("bad input: "+re);
	    }

	}
	
	
}
