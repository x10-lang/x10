/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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

    protected ConstructorDef_c(TypeSystem ts, Position pos,
	                         Ref<? extends ContainerType> container,
				 Flags flags, List<Ref<? extends Type>> formalTypes) {
        super(ts, pos, container, flags, formalTypes);
    }
    
    protected transient ConstructorInstance asInstance;

    public ConstructorInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createConstructorInstance(position(), Types.ref(this));
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
