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

import polyglot.util.SubtypeSet;
import polyglot.visit.CFGBuilder;

/**
 * A <code>Term</code> represents any Java expression or statement on which
 * dataflow can be performed.
 */
public interface Term extends Node
{
    /**
     * Indicates to dataflow methods that we are looking at the entry of a term.
     */
    public static final int ENTRY = 1;
    
    /**
     * Indicates to dataflow methods that we are looking at the exit of a term.
     */
    public static final int EXIT = 0;
    
    /**
     * Return the first direct subterm performed when evaluating this term. If
     * this term has no subterms, this should return null.
     * 
     * This method is similar to the deprecated entry(), but it should *not*
     * recursively drill down to the innermost subterm. The direct child visited
     * first in this term's dataflow should be returned.
     */
    public Term firstChild();

    /**
     * Visit this node, calling calling v.edge() for each successor in succs,
     * if data flows on that edge.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs);
    
    /**
     * Returns true if the term is reachable.  This attribute is null until after the reachability pass.
     *
     * @see polyglot.visit.ReachChecker
     */
    public Boolean reachable();

    /**
     * Set the reachability of this term.
     */
    public Term reachable(boolean reachability);
    
    /**
     * List of Types with all exceptions possibly thrown by this term.
     * The list is not necessarily correct until after exception-checking.
     * <code>polyglot.ast.NodeOps.throwTypes()</code> is similar, but exceptions
     * are not propagated to the containing node.
     */
    public SubtypeSet exceptions();
    public Term exceptions(SubtypeSet exceptions);
}
