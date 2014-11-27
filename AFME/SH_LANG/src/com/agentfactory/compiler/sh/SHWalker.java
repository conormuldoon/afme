// $ANTLR 2.7.5 (20050128): "shwalker.g" -> "SHWalker.java"$

package com.agentfactory.compiler.sh;

import java.util.HashSet;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
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
public class SHWalker extends antlr.TreeParser       implements SHWalkerTokenTypes
 {

	HashSet<FOS>set=new HashSet<FOS>();
	StringBuilder wb=new StringBuilder();
	
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
	
	/** walk list of hidden tokens in order, printing them out */
	private void dumpHidden(antlr.CommonHiddenStreamToken t) {
	  for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    out.print(t.getText());
	  }
	}
	
	private void printWait(){
		out.print(wb.toString());
		wb.setLength(0);
	}
	
	
	
	private void wait(AST p){
		antlr.CommonHiddenStreamToken t=((antlr.CommonASTWithHiddenTokens)p).getHiddenAfter();
		for ( ; t!=null ; t=Main.filter.getHiddenAfter(t) ) {
	    	wb.append(t.getText());
	  	}
	}
	
	
	private void add(String s){
		if(s.charAt(0)=='!')s=s.substring(1);
		set.add(FOS.createFOS(s));
	}
	
	
	private boolean commitCheck(String s){
		
		if(s.charAt(0)=='!'){
			s=s.substring(1);
		}
		FOS f=FOS.createFOS(s);
		for(FOS fos:set){
			if(f.matches(fos)){
				return true;
				}
		}
		return false;
	}
	
	
	private void ck(String s){
		if(s.charAt(0)=='!'){
			s=s.substring(1);
			out.print('!');
		}
		
		FOS f=FOS.createFOS(s);
		for(FOS fos:set){
			if(f.matches(fos)){
				out.print(s);
				return;
				}
		}
		
		out.print("BELIEF("+s+')');
	}
	
	private void check(String s){
		
		ck(s);
		printWait();
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
public SHWalker() {
	tokenNames = _tokenNames;
}

	public final void walk(AST _t) throws RecognitionException {
		
		AST walk_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			dumpHidden(Main.filter.getInitialHiddenToken());
			{
			_loop3:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case IS:
				case IMP:
				case NOT:
				case STRING:
				case INT:
				case IDENT:
				{
					rule(_t);
					_t = _retTree;
					break;
				}
				case ACT:
				{
					act(_t);
					_t = _retTree;
					break;
				}
				case PER:
				{
					per(_t);
					_t = _retTree;
					break;
				}
				case WORD:
				{
					word(_t);
					_t = _retTree;
					break;
				}
				case MOD:
				{
					mod(_t);
					_t = _retTree;
					break;
				}
				case LITERAL_import:
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
				case ESC:
				{
					AST tmp1_AST_in = (AST)_t;
					match(_t,ESC);
					_t = _t.getNextSibling();
					
						 String s=tmp1_AST_in.getText();
						 pr(tmp1_AST_in,s.substring(2,s.length()-1));
						
					break;
				}
				default:
				{
					break _loop3;
				}
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
	
	public final void rule(AST _t) throws RecognitionException {
		
		AST rule_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NOT:
			case STRING:
			case INT:
			case IDENT:
			{
				beliefs(_t);
				_t = _retTree;
				break;
			}
			case IS:
			case IMP:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IS:
			{
				{
				AST tmp2_AST_in = (AST)_t;
				match(_t,IS);
				_t = _t.getNextSibling();
				System.out.println("Belief labeling not supported");
				fos(_t,new StringBuilder());
				_t = _retTree;
				}
				break;
			}
			case IMP:
			{
				{
				AST tmp3_AST_in = (AST)_t;
				match(_t,IMP);
				_t = _t.getNextSibling();
				pr(tmp3_AST_in,"=>");
				{
				commit(_t);
				_t = _retTree;
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
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case REQ:
			{
				AST tmp4_AST_in = (AST)_t;
				match(_t,REQ);
				_t = _t.getNextSibling();
				System.out.println("Belief labeling not supported");
				commafos(_t,new StringBuilder());
				_t = _retTree;
				System.out.println("requires not supported");
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST tmp5_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp5_AST_in);
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
			match(_t,ACT);
			_t = _t.getNextSibling();
			pr(a,"ACTUATOR"+a.getText().substring(3));
			cls(_t);
			_t = _retTree;
			{
			_loop6:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp6_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					cls(_t);
					_t = _retTree;
					System.out.println("comma actuator feature not supported");
				}
				else {
					break _loop6;
				}
				
			} while (true);
			}
			AST tmp7_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp7_AST_in);
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
			match(_t,PER);
			_t = _t.getNextSibling();
			pr(a,"PERCEPTOR"+a.getText().substring(3));
			cls(_t);
			_t = _retTree;
			{
			_loop9:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp8_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					cls(_t);
					_t = _retTree;
					System.out.println("comma perceptor feature not supported");
				}
				else {
					break _loop9;
				}
				
			} while (true);
			}
			AST tmp9_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp9_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void word(AST _t) throws RecognitionException {
		
		AST word_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		StringBuilder sb=new StringBuilder();
		
		try {      // for error handling
			AST tmp10_AST_in = (AST)_t;
			match(_t,WORD);
			_t = _t.getNextSibling();
			pr(tmp10_AST_in,"");
			fos(_t,sb);
			_t = _retTree;
			add(sb.toString()); printWait();
				
			AST tmp11_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp11_AST_in,"");
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
		AST eq = null;
		
		try {      // for error handling
			m = (AST)_t;
			match(_t,MOD);
			_t = _t.getNextSibling();
			pr(m,"LOAD_MODULE"+m.getText().substring(3));
			fos(_t,null);
			_t = _retTree;
			{
			_loop12:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==DOT)) {
					AST tmp12_AST_in = (AST)_t;
					match(_t,DOT);
					_t = _t.getNextSibling();
					pr(tmp12_AST_in);
					fos(_t,null);
					_t = _retTree;
				}
				else {
					break _loop12;
				}
				
			} while (true);
			}
			eq = (AST)_t;
			match(_t,EQUALS);
			_t = _t.getNextSibling();
			pr(eq," ");
			cls(_t);
			_t = _retTree;
			AST tmp13_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp13_AST_in);
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
			match(_t,LITERAL_import);
			_t = _t.getNextSibling();
			pr(i,"IMPORT");
			cls(_t);
			_t = _retTree;
			{
			_loop16:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp14_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					cls(_t);
					_t = _retTree;
					System.out.println("comma import feature not supported");
				}
				else {
					break _loop16;
				}
				
			} while (true);
			}
			AST tmp15_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp15_AST_in);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void role(AST _t) throws RecognitionException {
		
		AST role_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST r = null;
		
		try {      // for error handling
			r = (AST)_t;
			match(_t,ROLE);
			_t = _t.getNextSibling();
			pr(r,"ROLE"+r.getText().substring(4));
			fos(_t,null);
			_t = _retTree;
			AST tmp16_AST_in = (AST)_t;
			match(_t,OCB);
			_t = _t.getNextSibling();
			pr(tmp16_AST_in);
			{
			_loop32:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case LITERAL_trigger:
				{
					trigger(_t);
					_t = _retTree;
					break;
				}
				case IS:
				case IMP:
				case NOT:
				case STRING:
				case INT:
				case IDENT:
				{
					rule(_t);
					_t = _retTree;
					break;
				}
				case ESC:
				{
					AST tmp17_AST_in = (AST)_t;
					match(_t,ESC);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					break _loop32;
				}
				}
			} while (true);
			}
			AST tmp18_AST_in = (AST)_t;
			match(_t,CCB);
			_t = _t.getNextSibling();
			pr(tmp18_AST_in);
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
			_loop36:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==DOT)) {
					AST tmp19_AST_in = (AST)_t;
					match(_t,DOT);
					_t = _t.getNextSibling();
					pr(tmp19_AST_in);
					fos(_t,null);
					_t = _retTree;
				}
				else {
					break _loop36;
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
	
	public final void fos(AST _t,
		StringBuilder sb
	) throws RecognitionException {
		
		AST fos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NOT:
			{
				AST tmp20_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getNextSibling();
				if(sb==null)pr(tmp20_AST_in);else {
					pr(tmp20_AST_in,"");sb.append(tmp20_AST_in.getText());
					}
					
				break;
			}
			case STRING:
			case INT:
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
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRING:
			{
				{
				AST tmp21_AST_in = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				if(sb==null)pr(tmp21_AST_in);else{
					 wait(tmp21_AST_in); sb.append(tmp21_AST_in.getText());
					 }
					
				}
				break;
			}
			case INT:
			case IDENT:
			{
				{
				{
				int _cnt43=0;
				_loop43:
				do {
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case INT:
					{
						AST tmp22_AST_in = (AST)_t;
						match(_t,INT);
						_t = _t.getNextSibling();
						if(sb==null)pr(tmp22_AST_in);else{
							 wait(tmp22_AST_in);
							 sb.append(tmp22_AST_in.getText());
							 }
							
						break;
					}
					case IDENT:
					{
						AST tmp23_AST_in = (AST)_t;
						match(_t,IDENT);
						_t = _t.getNextSibling();
						if(sb==null)pr(tmp23_AST_in);else{
							 
							 wait(tmp23_AST_in);
							 sb.append(tmp23_AST_in.getText());
							 }
							
						break;
					}
					default:
					{
						if ( _cnt43>=1 ) { break _loop43; } else {throw new NoViableAltException(_t);}
					}
					}
					_cnt43++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case OB:
				{
					AST tmp24_AST_in = (AST)_t;
					match(_t,OB);
					_t = _t.getNextSibling();
					if(sb==null)pr(tmp24_AST_in);else
						 { 
						 wait(tmp24_AST_in);
						 sb.append(tmp24_AST_in.getText());
						 }
					commafos(_t,sb);
					_t = _retTree;
					AST tmp25_AST_in = (AST)_t;
					match(_t,CB);
					_t = _t.getNextSibling();
					if(sb==null)pr(tmp25_AST_in);else{
						 wait(tmp25_AST_in);
						 sb.append(tmp25_AST_in.getText());
						 }
						
					break;
				}
				case COMMA:
				case SEMI:
				case DOT:
				case EQUALS:
				case IS:
				case IMP:
				case REQ:
				case OCB:
				case CB:
				case APOS:
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
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void beliefs(AST _t) throws RecognitionException {
		
		AST beliefs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
		try {      // for error handling
			StringBuilder sb=new StringBuilder();
			fos(_t,sb);
			_t = _retTree;
			check(sb.toString());
			{
			_loop19:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					c = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					sb.setLength(0); pr(c,"&");
					fos(_t,sb);
					_t = _retTree;
					check(sb.toString());
				}
				else {
					break _loop19;
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
	
	public final void commafos(AST _t,
		StringBuilder sb
	) throws RecognitionException {
		
		AST commafos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			fos(_t,sb);
			_t = _retTree;
			{
			_loop22:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp26_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					
						if(sb==null)pr(tmp26_AST_in);
						else{
							sb.append(tmp26_AST_in.getText());
							wait(tmp26_AST_in);
						}
						
						
					fos(_t,sb);
					_t = _retTree;
				}
				else {
					break _loop22;
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
		
			StringBuilder sb=new StringBuilder();
			String s1=null,s2=null,s3=null,s4=null;
			String m1=null,m2=null,m3=null,m4=null;
			
		
		try {      // for error handling
			{
			fos(_t,sb);
			_t = _retTree;
			
				s1=sb.toString();
				m1=wb.toString();
				wb.setLength(0);
				
				
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COMMA:
			{
				AST tmp27_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				wait(tmp27_AST_in);
						sb.setLength(0);
						m1=m1+tmp27_AST_in.getText();
					
						
				fos(_t,sb);
				_t = _retTree;
				
						s2=sb.toString();
						m2=wb.toString();
						
						wb.setLength(0);
						
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case COMMA:
				{
					AST tmp28_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					wait(tmp28_AST_in);
							sb.setLength(0);
							m2=m2+tmp28_AST_in.getText();
						
							
					fos(_t,sb);
					_t = _retTree;
					
							s3=sb.toString();
							m3=wb.toString();
							wb.setLength(0);
							
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case COMMA:
					{
						AST tmp29_AST_in = (AST)_t;
						match(_t,COMMA);
						_t = _t.getNextSibling();
						wait(tmp29_AST_in);
								sb.setLength(0);
								m3=m3+tmp29_AST_in.getText();
								
						fos(_t,sb);
						_t = _retTree;
						
								s4=sb.toString();
								m4=wb.toString();
								wb.setLength(0);
								
						break;
					}
					case SEMI:
					case REQ:
					case APOS:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					break;
				}
				case SEMI:
				case REQ:
				case APOS:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				break;
			}
			case SEMI:
			case REQ:
			case APOS:
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
			
					if(s2==null){
						if(commitCheck(s1)){
							out.print(s1+m1);
						}
						else {
							out.print("COMMIT(?Self,?Now,BELIEF(true),"+s1+m1);
						}
					}else if(s3==null){
						out.print("COMMIT(Self,Now,");
						ck(s1);
						out.print(m1+s2+m2);
						
					}else if(s4==null){
						out.print("COMMIT(Self,"+s1+m1);
						ck(s2);
						out.print(m2+s3+m3);
					}else{
						out.print("COMMIT("+s1+m1+s2+m2);
						ck(s3);
						out.print(m3+s4+m4);
					}
				
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case APOS:
			{
				AST tmp30_AST_in = (AST)_t;
				match(_t,APOS);
				_t = _t.getNextSibling();
				pr(tmp30_AST_in,",");
				fos(_t,null);
				_t = _retTree;
				AST tmp31_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getNextSibling();
				pr(tmp31_AST_in);
				fos(_t,null);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case COMMA:
				{
					AST tmp32_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					fos(_t,null);
					_t = _retTree;
					System.out.println("commitment ids not supported");
					break;
				}
				case SEMI:
				case REQ:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				break;
			}
			case SEMI:
			case REQ:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			
					out.print(')');
				
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
		StringBuilder sb=new StringBuilder();
		
		try {      // for error handling
			t = (AST)_t;
			match(_t,LITERAL_trigger);
			_t = _t.getNextSibling();
			pr(t, "TRIGGER");
			fos(_t,sb);
			_t = _retTree;
			check(sb.toString());
			AST tmp33_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
			pr(tmp33_AST_in);
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
		"ESC",
		"ACT",
		"COMMA",
		"SEMI",
		"PER",
		"MOD",
		"DOT",
		"EQUALS",
		"WORD",
		"\"import\"",
		"IS",
		"IMP",
		"REQ",
		"ROLE",
		"OCB",
		"CCB",
		"\"trigger\"",
		"NOT",
		"STRING",
		"INT",
		"IDENT",
		"OB",
		"CB",
		"APOS"
	};
	
	}
	
