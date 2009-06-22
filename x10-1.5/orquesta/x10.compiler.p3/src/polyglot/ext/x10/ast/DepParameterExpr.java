/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.TypeNode;
import polyglot.types.Ref;
import x10.constraint.XConstraint;

/**
 * @author vj Jan 9, 2005
 * 
 * [T](e){x:T; c}
 */
public interface DepParameterExpr extends Ambiguous {
	List<Formal> formals();
	DepParameterExpr formals(List<Formal> formals);

	Expr condition();
	DepParameterExpr condition(Expr cond);

	Ref<XConstraint> xconstraint();
}
