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

public class AIX_CXXCommandBuilder extends CXXCommandBuilder {
    
    public void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);

        if (usingXLC()) {
            cxxCmd.add("-qrtti=all");
            cxxCmd.add("-brtl");
        } else {
            cxxCmd.add("-maix64"); // Assume 64-bit
            cxxCmd.add("-Wl,-brtl");
        }
    }

    public void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);

        if (usingXLC()) {
            cxxCmd.add("-bbigtoc");
            cxxCmd.add("-lptools_ptr");
        } else {
            cxxCmd.add("-Wl,-bbigtoc");
            cxxCmd.add("-Wl,-lptools_ptr");
        }
    }
}
