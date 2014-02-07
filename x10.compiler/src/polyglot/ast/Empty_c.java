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

import java.util.List;

import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.PrettyPrinter;

/**
 * <code>Empty</code> is the class for a empty statement <code>(;)</code>.
 */
public class Empty_c extends Stmt_c implements Empty
{
    public Empty_c(Position pos) {
	super(pos);
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write(";");
    }

    public Term firstChild() {
        return null;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        return succs;
    }

    public String toString() {
	return ";";
    }

}
