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
 * DesignFile.java
 *
 * Created on 01 July 2005, 11:05
 */

package com.agentfactory.migration.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Conor Muldoon
 */
public class DesignFile {

	String firstLine;

	int len;

	/** Creates a new instance of DesignFile */
	public DesignFile(String fileName) {

		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResource(fileName).openStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line=line.trim();
				if (line.indexOf("=>") == -1 || line.startsWith("//")
						|| line.startsWith("/*") || line.equals(""))
					continue;
				if (firstLine == null)
					firstLine = line.substring(0, line.length() - 1);
				len++;
			}
			reader.close();

		} catch (IOException e) {

			e.printStackTrace();
		}catch (NullPointerException e){
			System.err.println("Could not create design file: "+fileName);
		}
	}

	public Collection<String> ruleFilter(DataInputStream input)
			throws IOException {

		Collection<String> coll = new ArrayList<String>();
		for (int i = input.readInt(); i-- > 0;) {
			String in = input.readUTF();

			if (in.equals(firstLine)) {
				for (int j = len - 1; j-- > 0;)
					input.readUTF();
				i -= (len - 1);
				continue;
			}
			coll.add(in);
		}

		return coll;
	}
}
