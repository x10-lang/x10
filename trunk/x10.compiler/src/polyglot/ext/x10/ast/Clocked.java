/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;
import polyglot.ast.Stmt;

/**
 * The node constructed for clocked (C) {S}.
 * TODO vj: Should take a list of clocks
 */
public interface Clocked extends Stmt {

    /** Get the clock. */
    List clocks();

    Clocked expr(List clocks);

    /** Get the statement. */
    Stmt stmt();

    Clocked stmt(Stmt stmt);
}
