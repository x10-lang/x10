/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.types.*;

/**
 * A <code>TypeNode</code> is the syntactic representation of a 
 * <code>Type</code> within the abstract syntax tree.
 */
public interface TypeNode extends Receiver, QualifierNode, Term
{
    /**
     * Return the type of this node, or null if no type has been
     * assigned yet.
     */
    Ref<? extends Type> typeRef();
    
    /** Short name of the type, or null if not a <code>Named</code> type. */
    String nameString();

    TypeNode typeRef(Ref<? extends Type> ref);
}
