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

import polyglot.ast.Do_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

/**
 * @author igor
 */
public class X10Do_c extends Do_c {

    /**
     * @param pos
     * @param body
     * @param cond
     */
    public X10Do_c(Position pos, Stmt body, Expr cond) {
        super(pos, body, cond);
        // TODO Auto-generated constructor stub
    }

    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (! ts.isSubtype(cond.type(), ts.Boolean(), tc.context())) {
            throw new SemanticException(
                    "Condition of do statement must have boolean type, and not " + cond.type() + ".",
                    cond.position());
        }

        return this;
    }

}
