/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.main.Report;
import polyglot.types.VarDef_c.ConstantValue;
import polyglot.util.Position;

/**
 * A <code>LocalInstance</code> contains type information for a local variable.
 */
public class LocalDef_c extends VarDef_c implements LocalDef
{
    /** Used for deserializing types. */
    protected LocalDef_c() { }

    public LocalDef_c(TypeSystem ts, Position pos,
	  		   Flags flags, Ref<? extends Type> type, Name name) {
        super(ts, pos, flags, type, name);
    }

    protected transient LocalInstance asInstance;

    public LocalInstance asInstance() {
        if (asInstance == null) {
            asInstance = ts.createLocalInstance(position(), Types.ref(this));
        }
        return asInstance;
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
        
        return "local " + flags.translate() + type + " " +
        name + cvStr;
    }
}
