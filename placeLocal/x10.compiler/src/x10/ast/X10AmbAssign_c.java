/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import polyglot.ast.AmbAssign_c;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

public class X10AmbAssign_c extends AmbAssign_c {

    public X10AmbAssign_c(Position pos, Expr left, Operator op, Expr right) {
	super(pos, left, op, right);
    }
    
    @Override
    public Node disambiguate(ContextVisitor ar) throws SemanticException {
	if (left instanceof Call) {
	    Call c = (Call) left;
	    if (c.target() instanceof Expr) {
		X10NodeFactory nf = (X10NodeFactory) ar.nodeFactory();
		return nf.SettableAssign(position(), (Expr) c.target(), c.arguments(), operator(), right());
	    }
	}
	
	return super.disambiguate(ar);
    }

}
