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

package x10cuda.ast;

import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10.visit.X10DelegatingVisitor;
import x10.util.StreamWrapper;
import x10cpp.ast.X10CPPDelFactory_c;
import x10cuda.visit.CUDACodeGenerator;

/**
 * @author Dave Cunningham
 */
public class X10CUDADelFactory_c extends X10CPPDelFactory_c {

    protected X10DelegatingVisitor makeCodeGenerator(CodeWriter w, Translator tr) {
        return new CUDACodeGenerator((StreamWrapper)w,tr);
    }

}

