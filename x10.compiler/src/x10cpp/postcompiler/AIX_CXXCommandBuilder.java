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

public class AIX_CXXCommandBuilder extends CXXCommandBuilder {
    
    AIX_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options, eq);
    }

    protected boolean useXLC() {
        return !options.cppCompiler.equals(X10CPPCompilerOptions.CPPCompiler.GCC);
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);

        if (useXLC()) {
            cxxCmd.add("-qrtti=all"); // AIX specific.
            if (options.wordSize.equals(X10CPPCompilerOptions.WordSize.FORCE_32)) {
                cxxCmd.add("-bmaxdata:0x80000000");
            }
            cxxCmd.add("-brtl"); // AIX specific.
        } else {
            cxxCmd.add("-maix64"); // Assume 64-bit
            cxxCmd.add("-Wl,-brtl");
        }
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);

        if (useXLC()) {
            cxxCmd.add("-bbigtoc");
            cxxCmd.add("-lptools_ptr");
        } else {
            cxxCmd.add("-Wl,-bbigtoc");
            cxxCmd.add("-Wl,-lptools_ptr");
        }
    }
}
