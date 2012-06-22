/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.ast.BooleanLit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.types.constraints.ConstraintManager;


/**
 * A boolean constant value (true or false)
 */
public final class BooleanValue extends ConstantValue {
    private final boolean val;
    
    BooleanValue(boolean b) {
        val = b;
    }

	public boolean value() {
		return val;
	}

	@Override
	public Boolean toJavaObject() { return Boolean.valueOf(val); }

	@Override
	public BooleanLit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos) {
	    type = Types.addSelfBinding(type, ConstraintManager.getConstraintSystem().makeLit(toJavaObject(), getLitType(ts)));
	    return (BooleanLit)nf.BooleanLit(pos, val).type(type);
	}

    @Override
    public Type getLitType(TypeSystem ts) {
        return ts.Boolean();
    }

    @Override
	public BooleanLit toUntypedLit(NodeFactory nf, Position pos) {
	    return (BooleanLit) nf.BooleanLit(pos, val);
	}

	@Override
	public boolean equals(Object that) {
	    if (that instanceof BooleanValue) {
	        return (((BooleanValue) that).val) == val;
	    } else {
	        return false;
	    }
	}
	
	@Override
	public int hashCode() {
	    return Boolean.valueOf(val).hashCode();
	}
	
	@Override
	public String toString() {
	    return val ? "true" : "false";
	}

}
