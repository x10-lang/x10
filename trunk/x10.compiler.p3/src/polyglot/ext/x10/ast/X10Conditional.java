/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Conditional;
import polyglot.ast.Expr;
import polyglot.ext.x10.visit.ExprFlattener;

public interface X10Conditional extends Conditional {

    Expr flatten(ExprFlattener.Flattener f);

}
