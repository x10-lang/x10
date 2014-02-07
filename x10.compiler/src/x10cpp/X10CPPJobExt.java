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

package x10cpp;

import java.util.ArrayList;
import java.util.List;

import polyglot.frontend.JobExt;
import polyglot.types.MethodDef;

public class X10CPPJobExt implements JobExt {

    private List<MethodDef> mainMethods = new ArrayList<MethodDef>();

    public X10CPPJobExt() { }

    public List<MethodDef> mainMethods() {
        return mainMethods;
    }

    public void addMainMethod(MethodDef md) {
        mainMethods.add(md);
    }
}
