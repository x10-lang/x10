/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.NodeOps;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * Addiitonal node operations to support new visitors.
 * 
 * @author vj
 *
 */
public interface X10NodeOps extends NodeOps {

	 /** Elaborate the types in the AST
	     *
	     * This method is called by the <code>enter()</code> method of the
	     * visitor.  The method should perform work that should be done
	     * before visiting the children of the node.  The method may return
	     * <code>this</code> or a new copy of the node on which
	     * <code>visitChildren()</code> and <code>leave()</code> will be
	     * invoked.
	     *
	     * @param tc The type checking visitor.
	     */
	    NodeVisitor typeElaborateEnter(TypeElaborator tc) throws SemanticException;

	    /**
	     * Elaborate the types in the AST.
	     *
	     * This method is called by the <code>leave()</code> method of the
	     * visitor.  The method should perform work that should be done
	     * after visiting the children of the node.  The method may return
	     * <code>this</code> or a new copy of the node which will be
	     * installed as a child of the node's parent.
	     *
	     * @param tc The type elaborator.
	     */
	    Node typeElaborateOverride(Node parent, TypeElaborator tc) throws SemanticException;
	    Node typeElaborate(TypeElaborator tc) throws SemanticException;
}
