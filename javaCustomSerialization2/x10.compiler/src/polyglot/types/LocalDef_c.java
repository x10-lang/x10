/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.main.Report;
import polyglot.util.Position;

/**
 * A <code>LocalInstance</code> contains type information for a local variable.
 */
public class LocalDef_c extends VarDef_c implements LocalDef
{
    private static final long serialVersionUID = -7608661445145740056L;

    /** Used for deserializing types. */
    protected LocalDef_c() { }

    protected LocalDef_c(TypeSystem ts, Position pos,
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

    @Override
    public LocalDef_c copy() {
        LocalDef_c res = (LocalDef_c) super.copy();
        res.asInstance = null;
        return res;
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
