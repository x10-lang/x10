/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;

public interface SubtypeTest extends Expr {
    
    boolean equals();

	public TypeNode supertype();

	public TypeNode subtype();

	public SubtypeTest supertype(TypeNode sup);

	public SubtypeTest subtype(TypeNode sub);

}
