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

import java.util.ArrayList;
import java.util.List;

import polyglot.types.Name;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import x10cuda.types.CUDAData;

/**
 * A <code>Block</code> represents a Java block statement -- an immutable
 * sequence of statements.
 */
public class Block_c extends AbstractBlock_c implements Block
{
    public Block_c(Position pos, List<Stmt> statements) {
	super(pos, statements);
    }

    /** Write the block to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("{");
	w.unifiedBreak(4, 1, " ", 1);
	w.begin(0);
	super.prettyPrint(w, tr);
	w.end();
	w.unifiedBreak(0, 1, " ", 1);
	w.write("}");
    }
}
