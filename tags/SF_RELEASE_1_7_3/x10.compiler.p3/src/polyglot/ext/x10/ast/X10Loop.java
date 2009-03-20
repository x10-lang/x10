/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Stmt;

/**
 * @author vj Dec 9, 2004
 * @author igor Jan 19, 2006
 */
public interface X10Loop extends CompoundStmt {
	Stmt body();
	X10Loop body(Stmt body);
	Formal formal();
	X10Loop formal(Formal formal);
	Expr domain();
	X10Loop domain(Expr domain);
	List/*<Stmt>*/ locals();
	X10Loop locals(List/*<Stmt>*/ locals);
}

