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

import java.util.Map;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.*;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

/** Visitor which performs type checking on the AST. */
public class TypeCheckPreparer extends ContextVisitor
{
	private Map<Node,Node> memo;
	
    public TypeCheckPreparer(Job job, TypeSystem ts, NodeFactory nf, Map<Node,Node> memo) {
        super(job, ts, nf);
        this.setMemo(memo);
    }
    
    @Override
    public Node override(Node parent, Node n) {
    	Node m = n.del().setResolverOverride(parent, this);
    	assert m == null || m == n : "setResolverOverride should not rewrite";
    	return m;
   }

    protected Node leaveCall(Node parent, Node old, final Node n, NodeVisitor v) throws SemanticException {
        n.del().setResolver(parent, v instanceof TypeCheckPreparer ? (TypeCheckPreparer) v : this);
        return n;
    }

	public void setMemo(Map<Node,Node> memo) {
		this.memo = memo;
	}

	public Map<Node,Node> getMemo() {
		return memo;
	}   
}
