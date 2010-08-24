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
public class InitializerDef_c extends Def_c
                                implements InitializerDef
{
    protected Ref<? extends ClassType> container;
    protected Flags flags;

    /** Used for deserializing types. */
    protected InitializerDef_c() { }

    public InitializerDef_c(TypeSystem ts, Position pos,
				 Ref<? extends ClassType> container, Flags flags) {
        super(ts, pos);
	this.container = container;
	this.flags = flags;
    }

    public Ref<? extends StructType> container() {
        return container;
    }
    
    InitializerInstance asInstance;
    
    public InitializerInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createInitializerInstance(position(), Types.ref(this));
        }
        return asInstance;
    }
    
    /**
     * @param container The container to set.
     */
    public void setContainer(Ref<? extends StructType> container) {
        this.container = (Ref<? extends ClassType>) container;
    }

    public Flags flags() {
        return flags;
    }

    /**
     * @param flags The flags to set.
     */
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public String toString() {
        return flags.translate() + "initializer";
    }
}
