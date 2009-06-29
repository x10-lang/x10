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

import polyglot.ast.Expr;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.types.Type;

/** The AST node for the X10 construct future (P) {e}
 *
 */
public interface Future extends Expr, RemoteActivityInvocation {


    /** Set the RemoteActivity's body */
    Future body(Expr body);

    /** Get the body of the RemoteActivity. */
    Expr body();
    
    StmtSeq stmt();
    /**
     * Update this node in place by changing its fields to reflect
     * a flattening of the place and body.
     * @param s
     */
    Future flatten(ExprFlattener.Flattener fc);
    
    /** return the type of the future */
    Type type();
}
