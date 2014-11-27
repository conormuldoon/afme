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

package com.agentfactory.cldc.afapl2;
import com.agentfactory.cldc.PerceptionManager;
import com.agentfactory.cldc.Perceptor;

public class TestPerceptor extends Perceptor {
    
    int count;
    
    public TestPerceptor(PerceptionManager manager){
        super(manager);
        count=0;
        
    }
    
    public void perceive() {
    	
       count++;
       
       if(count%7==0){
    	   adoptBelief("someBel");
    	   System.out.println("adopting someBel");
       }
      /* 
       if(count==10){
    	   adoptBelief("event(a,b)");
       	   System.out.println("adopting event(a,b)");
       }*/
       
       if(count%10==0){
    	   adoptBelief("event(a,b)");
    	   System.out.println("adopting event(a,b)");
    	   
       }
       
       if(count%15==0){
    	   adoptBelief("time");
    	   System.out.println("adopting time");
       }
       
        
    }
}

