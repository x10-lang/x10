/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Sep 29, 2004
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CompoundStmt;

/**
 * The node constructed for [ateach,foreach,async] clocked (C) [stmt].
 * @author Christian Grothoff
 */
public interface Clocked extends CompoundStmt {

    /** Get the clock. */
    List clocks();

    Clocked clocks(List clocks);
}
