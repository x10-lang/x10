/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

/**
 * Am immutable representation of a Java statement with a label.  A labeled
 * statement contains the statement being labeled and a string label.
 */
public interface Labeled extends CompoundStmt 
{
    /** The label. */
    Id labelNode();
    /** Set the label. */
    Labeled labelNode(Id label);
    
    /** The statement to label. */
    Stmt statement();
    /** Set the statement to label. */
    Labeled statement(Stmt statement);
}
