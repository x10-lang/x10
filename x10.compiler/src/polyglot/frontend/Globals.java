/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.frontend;

import polyglot.ast.NodeFactory;
import polyglot.main.Options;
import polyglot.types.TypeSystem;

public class Globals {
    /**
     * Thread-local Compiler object. To ensure the compiler is reentrant (and
     * thus can be embedded in Eclipse), this should be the only static field.
     */
    private static ThreadLocal<Compiler> compiler = new ThreadLocal<Compiler>();
    
    public static void initialize(Compiler c) {
        compiler.set(c);
    }
    
    private static Compiler Compiler() { return compiler.get(); }

    private static ExtensionInfo Extension() { return Compiler().sourceExtension(); }

    public static Options Options() { return Extension().getOptions(); }
}
