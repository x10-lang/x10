/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.ast;

import x10.types.Ref;
import x10.types.Type;

/**
 * A <code>CanonicalTypeNode</code> is a type node for a canonical type.
 */
public interface CanonicalTypeNode extends TypeNode
{
    public CanonicalTypeNode typeRef(Ref<? extends Type> type);
}
