/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import polyglot.ast.Node;

/**
 * A parser interface.  It defines one method, <code>parse()</code>,
 * which returns the root of the AST.
 */
public interface Parser
{
    /** Return the root of the AST */
    Node parse();
}
