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
 * A <code>MemberDef</code> allows accessing the state of a class member.
 */
public abstract class MemberDef_c extends Def_c implements MemberDef
{
    private static final long serialVersionUID = 3410658974280570706L;

    protected Ref<? extends ContainerType> container;
    protected Flags flags;

    /** Used for deserializing types. */
    protected MemberDef_c() { }

    public MemberDef_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends ContainerType> container, Flags flags) {
        super(ts, pos, errorPos);
        this.container = container;
        this.flags = flags;
    }

    final public Ref<? extends ContainerType> container() {
        return container;
    }
    
    /**
     * @param container The container to set.
     */
    final public void setContainer(Ref<? extends ContainerType> container) {
        this.container = container;
    }

    final public Flags flags() {
        return flags;
    }

    /**
     * @param flags The flags to set.
     */
    final public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public boolean staticContext() {
        return flags().isStatic();
    }

    public abstract String toString();
}
