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

import java.util.ArrayList;
import java.util.Properties;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;
import x10cpp.X10CPPCompilerOptions;

public class MacOSX_CXXCommandBuilder extends CXXCommandBuilder {

    public void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-msse2");
        cxxCmd.add("-mfpmath=sse");
        cxxCmd.add("-Wno-unused-value");
//        cxxCmd.add("-Wno-logical-op-parentheses"); gcc does not like this flag
    }

    public void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);

        for (PrecompiledLibrary pcl:options.x10libs) {
            if (options.x10_config.DEBUG && !options.x10_config.DEBUG_APP_ONLY) {
                cxxCmd.add("-Wl,-rpath");
                cxxCmd.add("-Wl,"+pcl.absolutePathToRoot+"/lib-dbg");
            }
            cxxCmd.add("-Wl,-rpath");
            cxxCmd.add("-Wl,"+pcl.absolutePathToRoot+"/lib");
        }
        
        // x10rt
        cxxCmd.add("-Wl,-rpath");
        cxxCmd.add("-Wl,"+options.distPath()+"/lib");
        cxxCmd.add("-Wl,-rpath");
        cxxCmd.add("-Wl,"+options.distPath()); // some libs end up with a "lib/" relative path
    }
}
