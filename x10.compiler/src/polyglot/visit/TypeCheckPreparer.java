/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
