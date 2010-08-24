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
 * A <code>MethodInstance</code> represents the type information for a Java
 * method.
 */
public class MethodDef_c extends ProcedureDef_c
                                implements MethodDef
{
    protected Name name;
    protected Ref<? extends Type> returnType;

    /** Used for deserializing types. */
    protected MethodDef_c() { }

    public MethodDef_c(TypeSystem ts, Position pos,
	 		    Ref<? extends StructType> container,
	                    Flags flags, Ref<? extends Type> returnType, Name name,
			    List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> throwTypes) {
        super(ts, pos, container, flags, formalTypes, throwTypes);
	this.returnType = returnType;
	this.name = name;
    }
    
    protected transient MethodInstance asInstance;
    
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createMethodInstance(position(), Types.ref(this));
        }
        return asInstance;
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

    public String toString() {
	String s = designator() + " " + flags.translate() + returnType + " " +
                   container() + "." + signature();

	if (! throwTypes.isEmpty()) {
	    s += " throws " + CollectionUtil.listToString(throwTypes);
	}

	return s;
    }

    public String signature() {
        return name + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }

    public String designator() {
        return "method";
    }
}
