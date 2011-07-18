/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import x10.types.X10CodeDef;

/**
 * A <code>InitializerInstance</code> contains the type information for a
 * static or anonymous initializer.
 */
public interface InitializerDef extends X10CodeDef, MemberDef
{
    InitializerInstance asInstance();
}
