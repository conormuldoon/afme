// $ANTLR 2.7.5 (20050128): "reversewalker.g" -> "RevWalker.java"$

package com.agentfactory.compiler.sh;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


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
public class RevWalker extends antlr.TreeParser       implements RevWalkerTokenTypes
 {

PrintStream out;
	File f;
	public void createFile(String fileName){
		
		try{
			out=new PrintStream(new FileOutputStream(f=new File(fileName)));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		try{
			System.out.print("Output File: ");
			System.out.println(f.getCanonicalFile().toString());
			out.flush();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	private void dump(AST p){
		dumpHidden(
			((antlr.CommonASTWithHiddenTokens)p).getHiddenAfter()
		);
	}
	
	private void pr(AST p,String s){
		out.print(s);
		dump(p);
	}
	
	private void pr(AST p) {
		pr(p,p.getText());
	}
	
	/** walk list of hidden tokens in order, printing them out */
	private void dumpHidden(antlr.CommonHiddenStreamToken t) {
	  for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    out.print(t.getText());
	  }
	}
	
	private void bPrint(StringBuilder sb,AST p){
		if(sb==null)pr(p);
		else{
			sb.append(p.getText());
			pr(p,"");
		} 
	}


public RevWalker() {
	tokenNames = _tokenNames;
}

	public final void walk(AST _t) throws RecognitionException {
		
		AST walk_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			dumpHidden(Main.filter.getInitialHiddenToken());
				 out.println("\nword BELIEF(?fos);");
				
			{
			_loop3:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case LITERAL_ACTUATOR:
				{
					act(_t);
					_t = _retTree;
					break;
				}
				case LITERAL_PERCEPTOR:
				{
					per(_t);
					_t = _retTree;
					break;
				}
				case LITERAL_LOAD_MODULE:
				{
					mod(_t);
					_t = _retTree;
					break;
				}
				case LITERAL_IMPORT:
				{
					imp(_t);
					_t = _retTree;
					break;
				}
				case ROLE:
				{
					role(_t);
					_t = _retTree;
					break;
				}
				case NOT:
				case LITERAL_BELIEF:
				{
					rule(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					break _loop3;
				}
				}
			} while (true);
			}
			AST tmp1_AST_in = (AST)_t;
			match(_t,Token.EOF_TYPE);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void act(AST _t) throws RecognitionException {
		
		AST act_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			a = (AST)_t;
			match(_t,LITERAL_ACTUATOR);
			_t = _t.getNextSibling();
			pr(a,"act"+a.getText().substring(8));
			cls(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void per(AST _t) throws RecognitionException {
		
		AST per_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			a = (AST)_t;
			match(_t,LITERAL_PERCEPTOR);
			_t = _t.getNextSibling();
			pr(a,"per"+a.getText().substring(9));
			cls(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void mod(AST _t) throws RecognitionException {
		
		AST mod_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST m = null;
		
		try {      // for error handling
			m = (AST)_t;
			match(_t,LITERAL_LOAD_MODULE);
			_t = _t.getNextSibling();
			pr(m,"mod"+m.getText().substring(11));
			fos(_t,null);
			_t = _retTree;
			out.print('=');
			cls(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void imp(AST _t) throws RecognitionException {
		
		AST imp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
		try {      // for error handling
			i = (AST)_t;
			match(_t,LITERAL_IMPORT);
			_t = _t.getNextSibling();
			pr(i,"import");
			cls(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void role(AST _t) throws RecognitionException {
		
		AST role_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp2_AST_in = (AST)_t;
			match(_t,ROLE);
			_t = _t.getNextSibling();
			pr(tmp2_AST_in,"role ");
			fos(_t,null);
			_t = _retTree;
			AST tmp3_AST_in = (AST)_t;
			match(_t,OCB);
			_t = _t.getNextSibling();
			pr(tmp3_AST_in);
			{
			_loop21:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case LITERAL_TRIGGER:
				{
					trigger(_t);
					_t = _retTree;
					break;
				}
				case NOT:
				case LITERAL_BELIEF:
				{
					rule(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					break _loop21;
				}
				}
			} while (true);
			}
			AST tmp4_AST_in = (AST)_t;
			match(_t,CCB);
			_t = _t.getNextSibling();
			pr(tmp4_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void rule(AST _t) throws RecognitionException {
		
		AST rule_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			beliefs(_t);
			_t = _retTree;
			AST tmp5_AST_in = (AST)_t;
			match(_t,IMP);
			_t = _t.getNextSibling();
			pr(tmp5_AST_in,">");
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_COMMIT:
			{
				commit(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_BELIEF:
			{
				belief(_t,true,null);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST tmp6_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp6_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void cls(AST _t) throws RecognitionException {
		
		AST cls_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			fos(_t,null);
			_t = _retTree;
			{
			_loop25:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==DOT)) {
					AST tmp7_AST_in = (AST)_t;
					match(_t,DOT);
					_t = _t.getNextSibling();
					pr(tmp7_AST_in);
					fos(_t,null);
					_t = _retTree;
				}
				else {
					break _loop25;
				}
				
			} while (true);
			}
			AST tmp8_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp8_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fos(AST _t,
		StringBuilder sb
	) throws RecognitionException {
		
		AST fos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRING:
			{
				AST tmp9_AST_in = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				bPrint(sb,tmp9_AST_in);
				break;
			}
			case IDENT:
			{
				{
				AST tmp10_AST_in = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				bPrint(sb,tmp10_AST_in);
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case OB:
				{
					fosbody(_t,true,sb);
					_t = _retTree;
					break;
				}
				case COMMA:
				case CB:
				case SEMI:
				case OCB:
				case DOT:
				case STRING:
				case IDENT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void item(AST _t,
		StringBuilder sb
	) throws RecognitionException {
		
		AST item_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NOT:
			{
				AST tmp11_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getNextSibling();
				pr(tmp11_AST_in);
				break;
			}
			case LITERAL_BELIEF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			belief(_t,false,sb);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void belief(AST _t,
		boolean b,StringBuilder sb
	) throws RecognitionException {
		
		AST belief_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST bel = null;
		
		try {      // for error handling
			bel = (AST)_t;
			match(_t,LITERAL_BELIEF);
			_t = _t.getNextSibling();
			
				if(b)pr(bel);else pr(bel,"");
				
			fosbody(_t,b,sb);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fosbody(AST _t,
		boolean b,StringBuilder sb
	) throws RecognitionException {
		
		AST fosbody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp12_AST_in = (AST)_t;
			match(_t,OB);
			_t = _t.getNextSibling();
			if(b)bPrint(sb,tmp12_AST_in);else pr(tmp12_AST_in,"");
			fos(_t,sb);
			_t = _retTree;
			{
			_loop13:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp13_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					
						bPrint(sb,tmp13_AST_in);
						
					fos(_t,sb);
					_t = _retTree;
				}
				else {
					break _loop13;
				}
				
			} while (true);
			}
			AST tmp14_AST_in = (AST)_t;
			match(_t,CB);
			_t = _t.getNextSibling();
			if(b)bPrint(sb,tmp14_AST_in); else pr(tmp14_AST_in,"");
				
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void beliefs(AST _t) throws RecognitionException {
		
		AST beliefs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			item(_t,null);
			_t = _retTree;
			{
			_loop16:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==AMPER)) {
					AST tmp15_AST_in = (AST)_t;
					match(_t,AMPER);
					_t = _t.getNextSibling();
					pr(tmp15_AST_in,",");
					item(_t,null);
					_t = _retTree;
				}
				else {
					break _loop16;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void commit(AST _t) throws RecognitionException {
		
		AST commit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		AST cm2 = null;
		AST cm3 = null;
		AST cm4 = null;
		AST cm5 = null;
		
		try {      // for error handling
			c = (AST)_t;
			match(_t,LITERAL_COMMIT);
			_t = _t.getNextSibling();
			pr(c,"");
			AST tmp16_AST_in = (AST)_t;
			match(_t,OB);
			_t = _t.getNextSibling();
			
					pr(tmp16_AST_in,"");
					boolean b=false;
					StringBuilder sb=new StringBuilder();
				
			fos(_t,sb);
			_t = _retTree;
			
					String s=sb.toString();
					
					if(!(s.equals("Self")||s.equals("?Self"))){
						b=true;
						out.print(s);
					}
				
			AST tmp17_AST_in = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			
				if(b)pr(tmp17_AST_in);
				else pr(tmp17_AST_in,"");
				sb.setLength(0);
				
				
			fos(_t,sb);
			_t = _retTree;
			
					s=sb.toString();
					if(b)out.print(s);
					else if(!(s.equals("Now")||s.equals("?Now"))){
							b=true;
							out.print(s);
					}
					
				
			cm2 = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			
				if(b)pr(cm2);
				else pr(cm2,"");
				sb.setLength(0);
				
			item(_t,sb);
			_t = _retTree;
			
					s=sb.toString();
					if(b)out.print(s);
					else if(!(s.equals("true"))){
							b=true;
							out.print(s);
					}
					
				
			cm3 = (AST)_t;
			match(_t,COMMA);
			_t = _t.getNextSibling();
			
					if(b)pr(cm3);
					else pr(cm3,"");
				
			fos(_t,null);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COMMA:
			{
				cm4 = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				pr(cm4,"\'");
				fos(_t,null);
				_t = _retTree;
				cm5 = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				pr(cm5);
				fos(_t,null);
				_t = _retTree;
				break;
			}
			case CB:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST tmp18_AST_in = (AST)_t;
			match(_t,CB);
			_t = _t.getNextSibling();
			pr(tmp18_AST_in,"");
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void trigger(AST _t) throws RecognitionException {
		
		AST trigger_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST t = null;
		
		try {      // for error handling
			t = (AST)_t;
			match(_t,LITERAL_TRIGGER);
			_t = _t.getNextSibling();
			pr(t,"trigger");
			item(_t,null);
			_t = _retTree;
			AST tmp19_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp19_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"ACTUATOR\"",
		"\"PERCEPTOR\"",
		"\"LOAD_MODULE\"",
		"\"IMPORT\"",
		"NOT",
		"\"BELIEF\"",
		"OB",
		"COMMA",
		"CB",
		"AMPER",
		"IMP",
		"SEMI",
		"ROLE",
		"OCB",
		"CCB",
		"\"TRIGGER\"",
		"DOT",
		"STRING",
		"IDENT",
		"\"COMMIT\""
	};
	
	}
	
