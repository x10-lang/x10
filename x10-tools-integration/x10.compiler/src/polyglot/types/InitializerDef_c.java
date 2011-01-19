/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.Position;

/**
 * A <code>InitializerInstance</code> contains the type information for a
 * static or anonymous initializer.
 */
public abstract class InitializerDef_c extends MemberDef_c implements InitializerDef
{
    private static final long serialVersionUID = -9122365198981873737L;

    /** Used for deserializing types. */
    protected InitializerDef_c() { }

    public InitializerDef_c(TypeSystem ts, Position pos, Ref<? extends ClassType> container, Flags flags) {
        super(ts, pos, container, flags);
    }

    InitializerInstance asInstance;
    
    public InitializerInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createInitializerInstance(position(), Types.ref(this));
        }
        return asInstance;
    }
    
    public String toString() {
        return flags.translate() + "initializer";
    }
}
