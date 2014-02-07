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
