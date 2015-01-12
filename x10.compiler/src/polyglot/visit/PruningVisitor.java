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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.visit;

import polyglot.ast.Node;

/**
 * A PruningVisitor is used to prune the traversal of the AST at a
 * particular node.  Returning a PruningVisitor from the
 * NodeVisitor.enter method ensures no children will be visited.
 */
public class PruningVisitor extends NodeVisitor
{
    public PruningVisitor() { super(); }
    @Override
    public Node override(Node parent, Node n) {
        return n;
    }
}
