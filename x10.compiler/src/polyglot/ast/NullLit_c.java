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
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.constants.ConstantValue;

/**
 * The Java literal <code>null</code>.
 */
public class NullLit_c extends Lit_c implements NullLit
{
    public NullLit_c(Position pos) {
	super(pos);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
	return type(tc.typeSystem().Null());
    }

    /** Get the value of the expression, as an object. */
    public Object objValue() {
	return null;
    }

    public String toString() {
	return "null";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("null");
    }

    public boolean isConstant() {
        return true;  
    }
    
    public ConstantValue constantValue() {
        return ConstantValue.makeNull();
    }
    
}
