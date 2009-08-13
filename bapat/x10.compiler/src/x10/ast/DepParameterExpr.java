/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.TypeNode;
import polyglot.types.Ref;
import x10.constraint.XConstraint;
import x10.types.TypeConstraint;

/**
 * @author vj Jan 9, 2005
 * 
 * [T](e){x:T; c}
 */
public interface DepParameterExpr extends Ambiguous {
	List<Formal> formals();
	DepParameterExpr formals(List<Formal> formals);

	List<Expr> condition();
	DepParameterExpr condition(List<Expr> cond);

	Ref<XConstraint> valueConstraint();
	DepParameterExpr valueConstraint(Ref<XConstraint> c);
	
	Ref<TypeConstraint> typeConstraint();
	DepParameterExpr typeConstraint(Ref<TypeConstraint> c);
}
