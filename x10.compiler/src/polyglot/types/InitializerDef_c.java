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
        super(ts, pos, pos, container, flags);
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
