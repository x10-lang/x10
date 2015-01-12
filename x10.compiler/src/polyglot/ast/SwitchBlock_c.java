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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.Context;
import polyglot.util.Position;

/**
 * A <code>SwitchBlock</code> is a list of statements within a switch.
 */
public class SwitchBlock_c extends AbstractBlock_c implements SwitchBlock
{
    public SwitchBlock_c(Position pos, List<Stmt> statements) {
	super(pos, statements);
    }
    
    /**
     * A <code>SwitchBlock</code> differs from a normal block in that 
     * declarations made in the context of the switch block are in the scope 
     * following the switch block. For example
     * <pre>
     * switch (i) { 
     *     case 0: 
     *       int i = 4; 
     *     case 1: 
     *       // i is in scope, but may not have been initialized.
     *     ...
     * } 
     * </pre>
     */
    public Context enterScope(Context c) {
        return c;
    }
    

}
