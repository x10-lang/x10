/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cpp.postcompiler;

import java.util.ArrayList;
import java.util.Properties;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;

public class Cygwin_CXXCommandBuilder extends CXXCommandBuilder {

    protected String defaultPostCompiler() {
        return "g++";
    }

    public void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-msse2");
        cxxCmd.add("-mfpmath=sse");
    }

}
