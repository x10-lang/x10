/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.ast;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import x10.visit.ExprFlattener;
import x10.visit.ExprFlattener.Flattener;

/**
 * An immutable representation of a binary operation.
 * @author vj
 *
 */
public interface X10Binary extends Binary {

    public boolean invert();
    public X10Binary invert(boolean invert);

	Expr flatten(ExprFlattener.Flattener f);
}
