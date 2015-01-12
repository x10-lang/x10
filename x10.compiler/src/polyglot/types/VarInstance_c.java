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

import polyglot.util.Position;
import x10.types.constants.ConstantValue;

public class VarInstance_c<T extends VarDef> extends Use_c<T> implements VarInstance<T> {
    private static final long serialVersionUID = 1662108421276493195L;

    public VarInstance_c(TypeSystem ts, Position pos, Ref<? extends T> def) {
        super(ts, pos, pos, def);
    }

    boolean constantValueSet;
    boolean isConstant;
    ConstantValue constantValue;
    boolean lval; // occurs in lval position

    public ConstantValue constantValue() {
        if (!constantValueSet && def.known()) {
            isConstant = def().isConstant();
            constantValue = def().constantValue();
            constantValueSet = true;
        }
        return constantValue;
    }

    public boolean isConstant() {
    	if (!constantValueSet && def.known()) {
            isConstant = def().isConstant();
            constantValue = def().constantValue();
            constantValueSet = true;
        }
        return isConstant;
    }

    public VarInstance<T> constantValue(ConstantValue o) {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.constantValueSet = true;
        v.isConstant = true;
        v.constantValue = o;
        return v;
    }

    public VarInstance<T> notConstant() {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.constantValueSet = true;
        v.isConstant = false;
        v.constantValue = null;
        return v;
    }

    Flags flags;

    public Flags flags() {
        if (flags == null) {
            return def().flags();
        }
        return flags;
    }

    public VarInstance<T> flags(Flags flags) {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.flags = flags;
        return v;
    }

    Name name;

    public Name name() {
        if (name == null) {
            return def().name();
        }
        return name;
    }

    public VarInstance<T> name(Name name) {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.name = name;
        return v;
    }

    protected Type type;

    public Type type() {
        if (type == null) {
            return Types.get(def().type());
        }
        return type;
    }

    public Type safeType() {
        if (type == null) {
            return Types.getCached(def().type());
        }
        return type;
    }

    public VarInstance<T> type(Type type) {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.type = type;
        return v;
    }
    
    public VarInstance<T> lval(boolean lval) {
        VarInstance_c<T> v = this.<VarInstance_c<T>>copyGeneric();
        v.lval = lval;
        return v;
    }

    public boolean lval() {
        return lval;
    }
}
