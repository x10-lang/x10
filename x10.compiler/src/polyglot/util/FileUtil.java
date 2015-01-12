/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;

import java.io.File;

public class FileUtil {
    /** Check that file in dir has the same name one would get by listing the directory contents.
     *  This prevents list.java from being found when looking for a class named List.
     */
    public static boolean checkNameFromRoot(File dir, File file) {
    	if (! file.exists()) {
    		return false;
    	}
        final File parentFile = file.getParentFile();
        if (parentFile==null) return false;
        if (parentFile.equals(dir)) {
            File[] ls = dir.listFiles();
            for (File f : ls) {
                if (f.getName().equals(file.getName())) {
                    return true;
                }
            }
            return false;
        }
        else {
            return checkNameFromRoot(dir, parentFile) &&
                checkNameFromRoot(parentFile, file);
        }
    }

}
