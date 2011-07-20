/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
        // A bold-face lie, but necessary until we rework the ConstantPropagator to properly represent NullLit's
        // See XTENLANG-2675.
        return false;  
    }
    
    public ConstantValue constantValue() {
        return ConstantValue.makeNull();
    }
    
}
