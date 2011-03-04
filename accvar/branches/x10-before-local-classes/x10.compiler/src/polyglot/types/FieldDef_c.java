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
    protected Ref<? extends StructType> container;
    protected InitializerDef initializer;

    /** Used for deserializing types. */
    protected FieldDef_c() { }
    
    public FieldDef_c(TypeSystem ts, Position pos,
			   Ref<? extends StructType> container,
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

    public Ref<? extends StructType> container() {
        return container;
    }


    /**
     * @param container The container to set.
     */
    public void setContainer(Ref<? extends StructType> container) {
        this.container = container;
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
