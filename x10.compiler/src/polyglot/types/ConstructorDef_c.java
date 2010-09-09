/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;

import polyglot.main.Report;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;

/**
 * A <code>ConstructorInstance</code> contains type information for a
 * constructor.
 */
public class ConstructorDef_c extends ProcedureDef_c
                                implements ConstructorDef
{
    /** Used for deserializing types. */
    protected ConstructorDef_c() { }

    public ConstructorDef_c(TypeSystem ts, Position pos,
	                         Ref<? extends ClassType> container,
				 Flags flags, List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> excTypes) {
        super(ts, pos, container, flags, formalTypes, excTypes);
    }
    
    protected transient ConstructorInstance asInstance;

    public ConstructorInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createConstructorInstance(position(), Types.ref(this));
        }
        return asInstance;
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
