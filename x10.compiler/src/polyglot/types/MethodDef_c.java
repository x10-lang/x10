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

import polyglot.main.Report;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import x10.types.MethodInstance;

/**
 * A <code>MethodInstance</code> represents the type information for a Java
 * method.
 */
public abstract class MethodDef_c extends ProcedureDef_c implements MethodDef
{
    private static final long serialVersionUID = -4600875186109814395L;

    protected Name name;
    protected Ref<? extends Type> returnType;

    /** Used for deserializing types. */
    protected MethodDef_c() { }

    protected MethodDef_c(TypeSystem ts, Position pos, Position errorPos,
	 		    Ref<? extends ContainerType> container,
	                    Flags flags, Ref<? extends Type> returnType, Name name,
			    List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> throwTypes) {
        super(ts, pos, errorPos, container, flags, formalTypes, throwTypes);
	this.returnType = returnType;
	this.name = name;
    }
    
    protected transient MethodInstance asInstance;
    
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createMethodInstance(position(), errorPosition(), Types.ref(this));
        }
        return asInstance;
    }

    @Override
    public MethodDef_c copy() {
        MethodDef_c res = (MethodDef_c) super.copy();
        res.asInstance = null;
        return res;
    }

    public Name name() {
        return name;
    }

    public Ref<? extends Type> returnType() {
        return returnType;
    }

    /**
     * @param name The name to set.
     */
    public void setName(Name name) {
        this.name = name;
        asInstance = null;
    }
    
    /**
     * @param returnType The returnType to set.
     */
    public void setReturnType(Ref<? extends Type> returnType) {
        this.returnType = returnType;
        asInstance = null;
    }

    @Override
    public void setFormalTypes(List<Ref<? extends Type>> formalTypes) {
        super.setFormalTypes(formalTypes);
        this.asInstance = null;
    }

    public String toString() {
	String s = designator() + " " + flags.translate() + returnType + " " +
                   container() + "." + signature();

	return s;
    }

    public String signature() {
        return name + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }

    public String designator() {
        return "method";
    }
    
}
