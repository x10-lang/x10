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
import x10.types.MethodInstance;

/**
 * A <code>MethodInstance</code> represents the type information for a Java
 * method.
 */
public class MethodDef_c extends ProcedureDef_c implements MethodDef
{
    private static final long serialVersionUID = -4600875186109814395L;

    protected Name name;
    protected Ref<? extends Type> returnType;

    /** Used for deserializing types. */
    protected MethodDef_c() { }

    public MethodDef_c(TypeSystem ts, Position pos,
	 		    Ref<? extends ContainerType> container,
	                    Flags flags, Ref<? extends Type> returnType, Name name,
			    List<Ref<? extends Type>> formalTypes) {
        super(ts, pos, container, flags, formalTypes);
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

	return s;
    }

    public String signature() {
        return name + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }

    public String designator() {
        return "method";
    }
}
