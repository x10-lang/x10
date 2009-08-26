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

/**
 * @author nystrom Dec 19, 2007
 * 
 */
public interface Existential extends Ambiguous, Expr {
    Expr condition();
    List<Formal> formals();
    Existential condition( Expr cond );
    Existential formals( List<Formal> formals);
    Existential reconstruct( List<Formal> formals, Expr cond);
}
