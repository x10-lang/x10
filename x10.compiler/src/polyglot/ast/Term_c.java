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

import polyglot.types.SemanticException;
import polyglot.util.*;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ExceptionChecker;

/**
 * A <code>Term</code> represents any Java expression or statement on which
 * dataflow can be performed.
 */
public abstract class Term_c extends Node_c implements Term
{
    public Term_c(Position pos) {
	super(pos);
    }
    
    protected Boolean reachable;

    /**
     * Visit this term in evaluation order.
     */
    public abstract <S> List<S> acceptCFG(CFGBuilder v, List<S> succs);

    /**
     * Return true if this term is reachable.  This attribute is not
     * guaranteed correct until after the reachability pass
     *
     * @see polyglot.visit.ReachChecker
     */
    public Boolean reachable() {
        return reachable;
    }

    /**
     * Set the reachability of this term.
     */
    public Term reachable(boolean reachability) {
        if (reachable!=null && this.reachable == reachability) {
            return this;
        }
        
        Term_c t = (Term_c) copy();
        t.reachable = reachability;
        return t;
    }

    /** Utility function to get the first entry of a list, or else alt. */
    public static Term listChild(List<? extends Term> l, Term alt) {
        Term c = (Term) CollectionUtil.firstOrElse(l, alt);
        return c;
    }
    
    protected SubtypeSet exceptions;
    
    public SubtypeSet exceptions() {
        return exceptions;
    }
    
    public Term exceptions(SubtypeSet exceptions) {
        Term_c n = (Term_c) copy();
        n.exceptions = new SubtypeSet(exceptions);
        return n;
    }
    
    public Node exceptionCheck(ExceptionChecker ec) {
        Term t = (Term) super.exceptionCheck(ec);
        return t.exceptions(ec.throwsSet());
    }
}
