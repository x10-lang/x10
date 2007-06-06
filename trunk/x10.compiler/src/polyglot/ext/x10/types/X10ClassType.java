/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.ClassType;

/** The representative of ClassType in the X10 type hierarchy. A class is a
 * reference; arrays are examples of references which are not classes.
 * 
 * @author vj
 *
 */
public interface X10ClassType extends ClassType, X10ReferenceType {
	Expr propertyExpr(int i);
	List<Expr> propertyExprs();
	X10ClassType propertyExprs(List<Expr> l);
}
