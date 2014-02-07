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

package polyglot.visit;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * Exception thrown when the CFG cannot be built.  This should be
 * a SemanticException, but is an error so it doesn't need to be declared
 * in the signature of Node.acceptCFG.
 */
public class CFGBuildError extends RuntimeException
{
    private static final long serialVersionUID = -1748858695130055618L;
    public final Position position;

    public CFGBuildError(String msg) {
        this(msg,null);
    }

    public CFGBuildError(String msg, Position position) {
        super(msg);
        this.position = position;
    }
}
