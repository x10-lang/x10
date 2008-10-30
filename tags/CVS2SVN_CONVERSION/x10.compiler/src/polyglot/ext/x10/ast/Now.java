/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Stmt;

/** The node constructed for the X10 construct now (c) S.
 * 
 */
public interface Now extends CompoundStmt {

    /** Get the expression. */
    Expr clock();

    Now clock(Expr clock);

    /** Get the expression. */
    Stmt body();

    Now body(Stmt stmt);
}
