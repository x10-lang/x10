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

public class Linux_CXXCommandBuilder extends CXXCommandBuilder {

    public void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        
        // [DC] http://sourceware.org/bugzilla/show_bug.cgi?id=15478
        // avoid runtime symbol resolution error from libx10.so to x10rt
        // on ubuntu gcc4.6
        cxxCmd.add("-Wl,--no-as-needed");
        
        if (!usingXLC()) {
            cxxCmd.add("-pthread");
            if (getPlatform().endsWith("_x86")) {
                cxxCmd.add("-msse2");
                cxxCmd.add("-mfpmath=sse");
            }
        }
    }

    public void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);

        for (PrecompiledLibrary pcl:options.x10libs) {
            if (options.x10_config.DEBUG && !options.x10_config.DEBUG_APP_ONLY) {
                cxxCmd.add("-Wl,--rpath");
                cxxCmd.add("-Wl,"+pcl.absolutePathToRoot+"/lib-dbg");
            }
            cxxCmd.add("-Wl,--rpath");
            cxxCmd.add("-Wl,"+pcl.absolutePathToRoot+"/lib");
        }
        
        // x10rt
        cxxCmd.add("-Wl,--rpath");
        cxxCmd.add("-Wl,"+options.distPath()+"/lib");

        cxxCmd.add("-Wl,-export-dynamic");
    }
}
