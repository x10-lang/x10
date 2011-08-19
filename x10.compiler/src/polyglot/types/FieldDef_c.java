/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.frontend.*;
import polyglot.main.Report;
import polyglot.util.Position;

/**
 * A <code>FieldInstance</code> contains type information for a field.
 */
public class FieldDef_c extends VarDef_c implements FieldDef
{
    private static final long serialVersionUID = -8958462141797079138L;

    protected Ref<? extends ContainerType> container;
    protected InitializerDef initializer;

    /** Used for deserializing types. */
    protected FieldDef_c() { }
    
    protected FieldDef_c(TypeSystem ts, Position pos,
			   Ref<? extends ContainerType> container,
	                   Flags flags, Ref<? extends Type> type, Name name) {
        super(ts, pos, flags, type, name);
        this.container = container;
    }
    
    public InitializerDef initializer() {
        return initializer;
    }

    public void setInitializer(InitializerDef initializer) {
        this.initializer = initializer;
    }
    
    protected transient FieldInstance asInstance;

    public FieldInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createFieldInstance(position(), Types.ref(this));
        }
        return asInstance;
    }

    @Override
    public FieldDef_c copy() {
        FieldDef_c res = (FieldDef_c) super.copy();
        res.asInstance = null;
        return res;
    }

    public Ref<? extends ContainerType> container() {
        return container;
    }

    @Override
    public void setConstantValue(x10.types.constants.ConstantValue constantValue) {
        super.setConstantValue(constantValue);
        this.asInstance = null;
    }

    @Override
    public void setFlags(Flags flags) {
        super.setFlags(flags);
        this.asInstance = null;
    }

    @Override
    public void setName(Name name) {
        super.setName(name);
        this.asInstance = null;
    }

    @Override
    public void setNotConstant() {
        super.setNotConstant();
        this.asInstance = null;
    }

    @Override
    public void setType(Ref<? extends Type> type) {
        super.setType(type);
        this.asInstance = null;
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(Ref<? extends ContainerType> container) {
        this.container = container;
        this.asInstance = null;
    }

    public String toString() {
        ConstantValue cv = constantRef.getCached();
        String cvStr = "";
        
        if (cv != null && cv.isConstant()) {
        	Object v = cv.value();
        	if (v instanceof String) {
        		String s = (String) v;

        		if (s.length() > 8) {
        			s = s.substring(0, 8) + "...";
        		}

        		v = "\"" + s + "\"";
        	}

        	cvStr = " = " + v;
        }
        
        return "field " + flags.translate() + type + " " +
        container + "." + name + cvStr;
    }
}
