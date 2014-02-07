/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Stmt;
import x10.types.AsyncDef;

/**
 * The node constructed for the X10 construct async {S}.
 */
public interface Async extends CompoundStmt,  Clocked {

    /** Set the Async's body */
    Async body(Stmt body);

    /** Get the body of the Async. */
    Stmt body();

    boolean clocked();

    AsyncDef asyncDef();
    Async asyncDef(AsyncDef ci);
}
