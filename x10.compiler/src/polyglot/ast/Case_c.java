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

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.constants.CharValue;
import x10.types.constants.ConstantValue;
import x10.types.constants.IntegralValue;

/**
 * A <code>Case</code> is a representation of a Java <code>case</code>
 * statement.  It can only be contained in a <code>Switch</code>.
 */
public class Case_c extends Stmt_c implements Case
{
    protected Expr expr;
    protected long value;

    public Case_c(Position pos, Expr expr) {
	super(pos);
	assert(true); // expr may be null for default case
	this.expr = expr;
    }

    /** Returns true iff this is the default case. */
    public boolean isDefault() {
	return this.expr == null;
    }

    /**
     * Get the case label.  This must should a constant expression.
     * The case label is null for the <code>default</code> case.
     */
    public Expr expr() {
	return this.expr;
    }

    /** Set the case label.  This must should a constant expression, or null. */
    public Case expr(Expr expr) {
	Case_c n = (Case_c) copy();
	n.expr = expr;
	return n;
    }

    /**
     * Returns the value of the case label.  This value is only valid
     * after type-checking.
     */
    public long value() {
	return this.value;
    }

    /** Set the value of the case label. */
    public Case value(long value) {
	Case_c n = (Case_c) copy();
	n.value = value;
	return n;
    }

    /** Reconstruct the statement. */
    protected Case_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Case_c n = (Case_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        Expr expr = (Expr) visitChild(this.expr, v);
        return reconstruct(expr);
    }

    /** Type check the statement. */
    public Node typeCheck(ContextVisitor tc) {
        if (expr == null) {
	    return this;
	}

	TypeSystem ts = tc.typeSystem();

	Type t = expr.type();
	Type st = tc.context().currentSwitchType();
	if (!ts.isSubtype(t, st)) {
	    Errors.issue(tc.job(),
	            new SemanticException("Case label must be of type "+st+" (same as the expression of the enclosing switch), not "+t+".", position()));
	}
//	if (!ts.isIntOrLess(t) && !ts.isUInt(t) && !ts.isChar(t)) {
//	    Errors.issue(tc.job(),
//	            new SemanticException("Case label must be a char or a (signed or unsigned) byte, short, or int, not "+t+".", position()));
//	}

	return this;
    }
    
    public Node checkConstants(ContextVisitor tc) {
        if (expr == null) {
            return this;
        }
        
        if (expr.isConstant()) {
            ConstantValue o = expr.constantValue();
            if (o instanceof IntegralValue) {
                return value(((IntegralValue) o).longValue());
            } else if (o instanceof CharValue) {
                return value(((CharValue) o).value());
            }
        }
        
        Errors.issue(tc.job(),
                new SemanticException("Case label must be an integral constant.",position()));
        return this;
    }

    public String toString() {
        if (expr == null) {
	    return "default:";
	}
	else {
	    return "case " + expr + ":";
	}
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (expr == null) {
	    w.write("default:");
	}
	else {
	    w.write("case ");
	    print(expr, w, tr);
	    w.write(":");
	}
    }

    public Term firstChild() {
        if (expr != null) return expr;
        return null;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (expr != null) {
            v.visitCFG(expr, this, EXIT);
        }

        return succs;
    }

}
