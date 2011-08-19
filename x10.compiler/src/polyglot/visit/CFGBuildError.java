/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
