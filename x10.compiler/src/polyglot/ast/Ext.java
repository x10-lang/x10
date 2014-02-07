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

package polyglot.ast;

import polyglot.util.CodeWriter;
import polyglot.util.Copy;

/**
 * <code>Ext</code> is the super type of all node extension objects.
 * It contains a pointer back to the node it is extending and a possibly-null
 * pointer to another extension node.
 */
public interface Ext extends Copy
{
    /** The node that we are extending. */
    Node node();

    /**
     * Initialize the extension object's pointer back to the node.
     * This also initializes the back pointers for all extensions of
     * the extension.
     */
    void init(Node node);

    /** An extension of this extension, or null. */
    Ext ext();

    /** Set the extension of this extension. */
    Ext ext(Ext ext);

    Ext copy();

    /**
     * Dump the AST node for debugging purposes.
     */
    void dump(CodeWriter w);
}
