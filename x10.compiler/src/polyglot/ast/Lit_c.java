/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import java.util.List;

import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/**
 * <code>Lit</code> represents any Java literal.
 */
public abstract class Lit_c extends Expr_c implements Lit
{
    public Lit_c(Position pos) {
	super(pos);
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
        return Precedence.LITERAL;
    }

    public Term firstChild() {
        return null;
    }

    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        return succs;
    }

    public boolean constantValueSet() {
        return true;
    }

    public boolean isConstant() {
	return true;
    }
    
    public abstract Object constantValue();
}
