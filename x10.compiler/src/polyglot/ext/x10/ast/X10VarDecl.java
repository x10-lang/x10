/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public interface X10VarDecl {
	/*
	 * Update the type of the vardecl from the type node.
	 */
	void pickUpTypeFromTypeNode(TypeChecker tc);
}
