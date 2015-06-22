/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10cpp.postcompiler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import polyglot.util.ErrorInfo;

import x10.ExtensionInfo;

/**
 * Represents a pre-compiled X10 library that is available 
 * for use in the current compilation.  A pre-compiled X10 library
 * currently is presented to the compiler as an on-disk directory
 * structure with a number of conventional pieces. These pieces
 * are found by reading a properties file that describes the library
 * in terms of relative paths from the directory containing the 
 * main library properties file.  
 */
public class PrecompiledLibrary extends PostCompileProperties {
    public static final String SOURCE_JAR_PROPERTY = "X10LIB_SRC_JAR";

    public final String absolutePathToRoot;
    public final String sourceJar;
    
    public PrecompiledLibrary(String absPath, Properties p) {
        super (p);
        absolutePathToRoot = absPath;
        sourceJar = p.getProperty(SOURCE_JAR_PROPERTY);
    }

    public void updateManifest(Set<String> manifest, ExtensionInfo ext) {
        manifest.add(sourceJar);
        File f = new File(absolutePathToRoot, sourceJar);
        try {
            JarFile jf = new JarFile(f);
            Enumeration<JarEntry> entries = jf.entries();
            String[] fileExt = ext.fileExtensions();
            files: while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                String name = je.getName();
                for (String fe:fileExt) {
                    if (name.endsWith(fe)) {
                        manifest.add(name);
                        continue files;
                    }
                }
            }
        } catch (IOException e) {
            ext.compiler().errorQueue().enqueue(ErrorInfo.WARNING, "Unable to process x10lib property " + SOURCE_JAR_PROPERTY + ": " + absolutePathToRoot + File.separatorChar + sourceJar);
        }
    }

}
