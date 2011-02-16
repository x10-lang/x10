/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Stmt;
import x10.types.AtDef;

/**
 * The node constructed for the X10 construct at (P) {S}.
 */
public interface AtStmt extends CompoundStmt, RemoteActivityInvocation {

    /** Set the At's body */
    AtStmt body(Stmt body);

    /** Get the body of the At. */
    Stmt body();

    /** Is the target place the place of the lexically enclosing finish (if any) */
    boolean isFinishPlace();

    AtDef atDef();

    AtStmt atDef(AtDef ci);
}
