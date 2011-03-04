/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.ast;

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.SemanticException;

/**
 * Utility class which is used to disambiguate ambiguous
 * AST nodes (Expr, Type, Receiver, Qualifier, Prefix).
 */
public interface Disamb
{
    /**
     * Disambiguate the prefix and name into a unambiguous node of the
     * appropriate type.
     * @return An unambiguous AST node, or null if disambiguation fails.
     */
    Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos,
            Node prefix, Id name) throws SemanticException;
}
