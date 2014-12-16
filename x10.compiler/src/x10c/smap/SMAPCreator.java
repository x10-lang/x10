/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package x10c.smap;

import java.io.File;
import java.util.List;

public class SMAPCreator {
    /**
     * @param filename
     * @param pathPrefix
     * @param elems
     * @param fileExten file name extension of the original source file; should not have a leading "."
     * @return the SMAP attribute contents, as specified by JSR 045
     */
    public static String get(String filename, String pathPrefix, List<LineElem> elems, String fileExten) {
        String name= filename;
        boolean path= false;
        if (filename.indexOf("/") != -1) { // path name included
            name= filename.substring(filename.lastIndexOf("/") + 1);
            if (pathPrefix != null)
            	filename = filename.substring(pathPrefix.length() + 1);

            filename = replaceAll(filename, File.separator);

            path= true;
        }

        String info= "SMAP\n";
        info+= name + ".java\n";
        info+= fileExten + "\n";
        info+= "*S " + fileExten + "\n"; 
        info+= "*F\n";
        if (path)
            info+= "+ ";
        info+= "1 " + name + "." + fileExten + "\n";
        if (path)
        	info+= filename + "." + fileExten + "\n";
        
        info+= "*L\n";

        for(LineElem elem: elems) {
            info+= elem.getOrigSrcStart() + "#1:" + elem.getJavaStart() + "," + elem.getIncr() + "\n";
        }

        info+= "*E\n";
        return info;
    }
    
    static String replaceAll(String filename, String sep) {
    	String ret = "";
    	String[] names = filename.split("/");
    	for(int i = 0; i < names.length; i++ ) {
    		if (i == 0)
    			ret = names[0];
    		else
    			ret += sep+ names[i];
    	}
    	return ret;
    }
}
