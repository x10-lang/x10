/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.TypeNode;

/**
 * The AST node created for the X10 type constructor: nullable X.
 *
 * @author vj
 */
public interface NullableNode extends X10TypeNode {
	TypeNode base();
	NullableNode base(TypeNode base);
}

