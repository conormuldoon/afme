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
import java.util.Hashtable;


/** This class stores information related to AFME FOSs.
 * 
 * @author Conor Muldoon
 *
 */
public class FOS{
    static final char COMMA = ',';
    static final char OPEN_BRACKET = '(';
    static final char CLOSE_BRACKET = ')';
    static final byte SIZE=4;

    static Hashtable variables=new Hashtable();

    String functor;
    FOS[]arguments;
    int tail;
    int cur;

    /** Returns an instance of FOS for the specified string representation.
     * 
     * @param string a string representation of the FOS.
     * @return a previous instance if the string representation a variable and it has been created before, 
     * a new instance otherwise.
     */
    public static FOS createFOS(String string){
        if(variables.containsKey(string))return (FOS)variables.get(string);
        FOS fos=new FOS(string);
        if(fos.isVariable())variables.put(string, fos);
        return fos;
    }



    private FOS(String string){
        arguments=new FOS[SIZE];
        int startIndex=string.indexOf(OPEN_BRACKET);
        if (startIndex==-1)functor=string.trim();
        else {
            functor=string.substring(0,startIndex).trim();
            int endIndex = string.lastIndexOf(CLOSE_BRACKET);
            string=string.substring(startIndex+1,endIndex).trim();
            int bracketCount=0;
            char character;
            int index=0;
            for(int i=0;i<string.length();i++){
                character = string.charAt(i);
                if(character==COMMA&&bracketCount==0){
                    add(createFOS(string.substring(index,i)));
                    index=i+1;
                } else {
                    if(character==OPEN_BRACKET)bracketCount++;
                    else if(character==CLOSE_BRACKET)bracketCount--;
                }
            }
            add(createFOS(string.substring(index).trim()));
        }
    }

    private boolean isVariable(){
        return tail==0&&functor.charAt(0)=='?';
    }

    private void add(FOS fos){
        if (tail == arguments.length-1){
            FOS[] newArray = new FOS[arguments.length<<1];
            System.arraycopy(arguments, 0, newArray, 0, tail);
            arguments=newArray;
        }
        arguments[tail] = fos;
        tail++;
    }

 
    /** Checks whether the FOS matches the specified FOS.
     * 
     * @param f the specified FOS.
     * @return true if the FOSs match, false otherwise.
     */
    public boolean matches(FOS f) {
        if(f.isVariable()||isVariable())return true;
        if(!(f.functor.equals(functor)&&tail==f.tail))return false;
        for(int i=tail;i-->0;)
            if(!arguments[i].matches(f.arguments[i]))return false;
        return true;
    }

   

 
    

}


