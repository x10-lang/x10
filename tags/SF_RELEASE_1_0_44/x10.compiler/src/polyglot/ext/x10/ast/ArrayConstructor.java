/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;


import polyglot.ast.Expr;
import polyglot.ast.TypeNode;


/** An immutable representation of an X10 Array constructor, which has
 * a basetype for the array, a distribution for the array, 
 * an (optional) index variable (whose type is the index type of the array),
 * and a Block.
 * 
 * @author vj Dec 9, 2004
 * 
 */
public interface ArrayConstructor extends Expr {
	TypeNode arrayBaseType();
	Expr distribution();
	Expr initializer();
	ArrayConstructor arrayBaseType( TypeNode t);
	ArrayConstructor distribution( Expr e);
	ArrayConstructor initializer( Expr e);
    boolean isSafe();
    boolean isValue();
	//TODO: vj Determine if this needs a CodeInstance as well.

}
