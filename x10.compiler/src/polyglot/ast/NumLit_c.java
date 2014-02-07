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

import polyglot.util.CodeWriter;
import polyglot.util.Position;

/**
 * An integer literal: longs, ints, shorts, bytes, and chars.
 */
public abstract class NumLit_c extends Lit_c implements NumLit
{
    protected long value;

    public NumLit_c(Position pos, long value) {
	super(pos);
	this.value = value;
    }

    /** Get the value of the expression. */
    public long longValue() {
	return this.value;
    }

    public void dump(CodeWriter w) {
        super.dump(w);

	w.allowBreak(4, " ");
	w.begin(0);
	w.write("(value " + value + ")");
	w.end();
    }
}
