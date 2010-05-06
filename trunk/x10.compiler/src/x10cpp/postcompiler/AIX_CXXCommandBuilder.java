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

public class AIX_CXXCommandBuilder extends CXXCommandBuilder {
    public AIX_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options, eq);
        assert (CXXCommandBuilder.PLATFORM.startsWith("aix_"));
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        
        if (USE_XLC) {
            cxxCmd.add("-qrtti=all"); // AIX specific.
            if (USE_32BIT) {
                cxxCmd.add("-bmaxdata:0x80000000");
            }
            cxxCmd.add("-brtl"); // AIX specific.
        } else {
            cxxCmd.add("-Wno-long-long");
            cxxCmd.add("-Wno-unused-parameter");
            cxxCmd.add("-maix64"); // Assume 64-bit
            cxxCmd.add("-Wl,-brtl");
        }     
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        
        if (USE_XLC) {
            cxxCmd.add("-bbigtoc");
            cxxCmd.add("-lptools_ptr");
        } else {
            cxxCmd.add("-Wl,-bbigtoc");
            cxxCmd.add("-Wl,-lptools_ptr");
        }
    }
}
