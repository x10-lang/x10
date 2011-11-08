/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.util.Position;
import polyglot.util.TypedList;

/**
 * A <code>ProcedureInstance_c</code> contains the type information for a Java
 * procedure (either a method or a constructor).
 */
public abstract class ProcedureDef_c extends MemberDef_c implements ProcedureDef
{
    private static final long serialVersionUID = 7146402627770404357L;

    protected List<Ref<? extends Type>> formalTypes;

    /** Used for deserializing types. */
    protected ProcedureDef_c() { }

    public ProcedureDef_c(TypeSystem ts, Position pos, Position errorPos,
            Ref<? extends ContainerType> container,
			       Flags flags, List<Ref<? extends Type>> formalTypes) {
        super(ts, pos, errorPos, container, flags);
        this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
    }
    
    public List<Ref<? extends Type>> formalTypes() {
        return Collections.unmodifiableList(formalTypes);
    }

    /**
     * @param formalTypes The formalTypes to set.
     */
    public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
        this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
    }
}
