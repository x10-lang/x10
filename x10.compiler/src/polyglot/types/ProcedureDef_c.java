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

import java.util.*;

import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.types.constraints.CNativeRequirementCollection;
import x10.types.constraints.CRequirement;
import x10.types.constraints.CRequirementCollection;

/**
 * A <code>ProcedureInstance_c</code> contains the type information for a Java
 * procedure (either a method or a constructor).
 */
public abstract class ProcedureDef_c extends MemberDef_c implements ProcedureDef
{
    private static final long serialVersionUID = 7146402627770404357L;

    protected List<Ref<? extends Type>> formalTypes;
    protected List<Ref<? extends Type>> throwTypes;

    /** Used for deserializing types. */
    protected ProcedureDef_c() { }

    public ProcedureDef_c(TypeSystem ts, Position pos, Position errorPos,
            Ref<? extends ContainerType> container,
			       Flags flags, List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> throwTypes) {
        super(ts, pos, errorPos, container, flags);
        this.formalTypes = TypedList.copyAndCheck(formalTypes, Ref.class, true);
        this.throwTypes = TypedList.copyAndCheck(throwTypes, Ref.class, true);
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

    public List<Ref<? extends Type>> throwTypes() {
        return Collections.unmodifiableList(throwTypes);
    }

    /**
     * @param throwTypes The throwTypes to set.
     */
    public void setThrowTypes(List<Ref<? extends Type>> throwTypes) {
        this.throwTypes = TypedList.copyAndCheck(throwTypes, Ref.class, true);
    }

	CNativeRequirementCollection requirements = new CNativeRequirementCollection();
	@Override
	public CRequirementCollection requirements() {
		return this.requirements;
	}

}
