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

import java.util.List;

import polyglot.main.Report;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;

/**
 * A <code>ConstructorInstance</code> contains type information for a
 * constructor.
 */
public abstract class ConstructorDef_c extends ProcedureDef_c implements ConstructorDef
{
    private static final long serialVersionUID = -6672601102313722506L;

    /** Used for deserializing types. */
    protected ConstructorDef_c() { }

    protected ConstructorDef_c(TypeSystem ts, Position pos, Position errorPos,
	                         Ref<? extends ContainerType> container,
				 Flags flags, List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> throwTypes) {
        super(ts, pos, errorPos, container, flags, formalTypes, throwTypes);
    }
    
    protected transient ConstructorInstance asInstance;

    public ConstructorInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createConstructorInstance(position(), errorPosition(), Types.ref(this));
        }
        return asInstance;
    }

    @Override
    public ConstructorDef_c copy() {
        ConstructorDef_c res = (ConstructorDef_c) super.copy();
        res.asInstance = null;
        return res;
    }

    @Override
    public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
        super.setFormalTypes(formalTypes);
        this.asInstance = null;
    }

    public String toString() {
	return designator() + " " + flags.translate() + signature();
    }
    
    public String signature() {
        return container + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }

    public String designator() {
        return "constructor";
    }
}
