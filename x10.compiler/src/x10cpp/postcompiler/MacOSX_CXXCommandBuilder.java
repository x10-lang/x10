/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;
import x10cpp.X10CPPCompilerOptions;

public class MacOSX_CXXCommandBuilder extends CXXCommandBuilder {

    MacOSX_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options,eq);
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        if (options.wordSize.equals(X10CPPCompilerOptions.WordSize.FORCE_32)) {
            cxxCmd.add("-arch");
            cxxCmd.add("i386");
        } else if (options.wordSize.equals(X10CPPCompilerOptions.WordSize.FORCE_64)) {
            cxxCmd.add("-arch");
            cxxCmd.add("x86_64");
        }
        cxxCmd.add("-msse2");
        cxxCmd.add("-mfpmath=sse");
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);

        // Support for loading shared libraries from x10.dist/lib
        cxxCmd.add("-Wl,-rpath");
        cxxCmd.add("-Wl,"+X10_DIST+"/lib");
        cxxCmd.add("-Wl,-rpath");
        cxxCmd.add("-Wl,"+X10_DIST+""); // some libs end up with a "lib/" relative path
    }
}
