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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.List;

import polyglot.util.Position;

public abstract class ConstructorInstance_c extends ProcedureInstance_c<ConstructorDef> implements ConstructorInstance {
    private static final long serialVersionUID = -702966148217075519L;

    public ConstructorInstance_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends ConstructorDef> def) {
        super(ts, pos, errorPos, def);
    }

    protected Flags flags;
    protected ContainerType container;
    
    public ConstructorInstance container(ContainerType container) {
        ConstructorInstance_c p = (ConstructorInstance_c) copy();
        p.container = container;
        return p;
    }

    public ContainerType container() {
        if (this.container == null) {
            return Types.get(def().container());
        }
        return this.container;
    }
    
    public ConstructorInstance flags(Flags flags) {
        ConstructorInstance_c p = (ConstructorInstance_c) copy();
        p.flags = flags;
        return p;
    }

    public Flags flags() {
        if (this.flags == null) { 
            return def().flags();
        }
        return this.flags;
    }
    
    public ConstructorInstance formalTypes(List<Type> formalTypes) {
        return (ConstructorInstance) super.formalTypes(formalTypes);
    }
    
    public ConstructorInstance throwTypes(List<Type> throwTypes) {
        return (ConstructorInstance) super.throwTypes(throwTypes);
    }
    
    public ConstructorInstance instantiate(ClassType objectType,
    		List<Type> argumentTypes) throws SemanticException {
    	return this;
    }
    protected ConstructorInstance origCI;
    public void setOrigMI(ConstructorInstance ci) {
    	this.origCI = ci;
    }
    public ConstructorInstance origMI() {
    	return origCI;
    }
}
