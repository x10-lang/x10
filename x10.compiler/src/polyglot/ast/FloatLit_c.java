/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.constants.ConstantValue;

/** 
 * A <code>FloatLit</code> represents a literal in java of type
 * <code>float</code> or <code>double</code>.
 */
public abstract class FloatLit_c extends Lit_c implements FloatLit
{
    protected FloatLit.Kind kind;
    protected double value;

    public FloatLit_c(Position pos, FloatLit.Kind kind, double value) {
	super(pos);
	assert(kind != null);
	this.kind = kind;
	this.value = value;
    }

    /** Get the kind of the literal. */
    public FloatLit.Kind kind() {
	return this.kind;
    }

    /** Set the kind of the literal. */
    public FloatLit kind(FloatLit.Kind kind) {
	FloatLit_c n = (FloatLit_c) copy();
	n.kind = kind;
	return n;
    }

    /** Get the value of the expression. */
    public double value() {
	return this.value;
    }

    /** Set the value of the expression. */
    public FloatLit value(double value) {
	FloatLit_c n = (FloatLit_c) copy();
	n.value = value;
	return n;
    }

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
	return Double.toString(value);
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (kind == FLOAT) {
	    w.write(Float.toString((float) value) + "F");
	}
	else if (kind == DOUBLE) {
	    w.write(Double.toString(value));
	}
	else {
	    throw new InternalCompilerError("Unrecognized FloatLit kind " +
		kind);
	}
    }

    public ConstantValue constantValue() {
      if (kind == FLOAT) {
        return ConstantValue.makeFloat((float)value);
      }
      else {
        return ConstantValue.makeDouble(value);
      }
    }

    public Precedence precedence() {
        if (value < 0) {
            return Precedence.UNARY;
        }
        else {
            return Precedence.LITERAL;
        }
    }

}
