/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Ambiguous;
import polyglot.ast.TypeNode;

/**
 * The AST node created for the X10 type constructor: nullable X.
 *
 * @author vj
 */
public interface NullableNode extends TypeNode, Ambiguous, ContainsType {
	TypeNode base();
	NullableNode base(TypeNode base);
}

