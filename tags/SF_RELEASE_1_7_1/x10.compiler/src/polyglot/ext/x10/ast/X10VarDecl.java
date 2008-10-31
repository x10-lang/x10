/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public interface X10VarDecl extends Node {
	/*
	 * Update the type of the vardecl from the type node.
	 */
	X10VarDecl pickUpTypeFromTypeNode(TypeChecker tc);
}
