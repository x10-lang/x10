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

public class SunOS_CXXCommandBuilder extends CXXCommandBuilder {

    public SunOS_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options, eq);
        assert (CXXCommandBuilder.PLATFORM.startsWith("sunos_"));
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-Wno-long-long");
        cxxCmd.add("-Wno-unused-parameter");
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        cxxCmd.add("-lrt");
    }
}
