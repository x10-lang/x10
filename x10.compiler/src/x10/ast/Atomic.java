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

import java.util.List;

import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Stmt;

/** The node constructed for the X10 construct atomic(P) {S}.
 * @author Christian Grothoff
 */
public interface Atomic extends CompoundStmt {
    
    /** Set the Atomic's body */
    Atomic body(Stmt body);

    /** Set the Atomic's identifier list */
    //for data-centric synchronization
    Atomic identifierList(List<Id> idlist);
    
    /** Get the body of the Atomic. */
    Stmt body();
}
