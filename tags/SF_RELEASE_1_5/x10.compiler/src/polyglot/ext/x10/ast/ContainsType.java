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

/**
 * Should be implemented by any X10TypeNode that has a child TypeNode.
 * 
 * @author vj
 *
 */
public interface ContainsType {
	/**
	 * Propagate the change, if any, in the enclosed type to a change in
	 * the type of this. Called by TypePropagator visitor.
	 * @return new node with its type set.
	 */
	Node propagateTypeFromBase();

}
