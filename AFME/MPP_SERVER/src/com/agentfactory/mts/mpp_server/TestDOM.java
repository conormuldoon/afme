// Copyright:   Copyright (c) 1996-2006 The Agent Factory Working Group. All rights reserved.
// Licence:     This file is free software; you can redistribute it and/or modify
//              it under the terms of the GNU Lesser General Public License as published by
//              the Free Software Foundation; either version 2.1, or (at your option)
//              any later version.
//
//              This file is distributed in the hope that it will be useful,
//              but WITHOUT ANY WARRANTY; without even the implied warranty of
//              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//              GNU Lesser General Public License for more details.
//
//              You should have received a copy of the GNU Lesser General Public License
//              along with Agent Factory Micro Edition; see the file COPYING.  If not, write to
//              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//              Boston, MA 02111-1307, USA.

/*
 * TestDOM.java
 *
 * Created on 25 February 2005, 11:47
 */

package com.agentfactory.mts.mpp_server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 *
 * @author Conor Muldoon
 */
public class TestDOM {
    
    /** Creates a new instance of TestDOM */
    public TestDOM() {
    }
    
    public static void main(String[]args){
        try{
            DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
            Document doc= impl.createDocument(null, "registeredPlatforms", null);
            
            Element regPlatforms = doc.getDocumentElement();
            Element platform=doc.createElement("platform");
            
            Text text = doc.createTextNode("calculateFibonacci");
            platform.appendChild(text);
            regPlatforms.appendChild(platform);
            
            Element params = doc.createElement("params");
            platform.appendChild(params);
            
            Element param = doc.createElement("param");
            params.appendChild(param);
            
            Element value = doc.createElement("value");
            param.appendChild(value);
            
           
            
            File file;
            OutputStream out = new FileOutputStream(file=new File("requestFile.xml"));
            System.out.println("File: "+file.getAbsolutePath());
            OutputFormat fmt = new OutputFormat(doc);
            XMLSerializer serializer = new XMLSerializer(out, fmt);
            serializer.serialize(doc);
            
            
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
    
}
