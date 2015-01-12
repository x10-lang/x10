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

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * A <code>VarInstance</code> contains type information for a variable.  It may
 * be either a local or a field.
 */
public abstract class VarDef_c extends Def_c implements VarDef
{
    private static final long serialVersionUID = 340662366820442336L;

    protected Flags flags;
    protected Ref<? extends Type> type;
    protected Name name;
    protected Ref<ConstantValue> constantRef;

    /** Used for deserializing types. */
    protected VarDef_c() { }

    public VarDef_c(TypeSystem ts, Position pos,
	                 Flags flags, Ref<? extends Type> type, Name name) {
    	super(ts, pos, pos);
    	this.flags = flags;
    	this.type = type;
    	this.name = name;
    	this.constantRef = Types.<ConstantValue>lazyRef(null);
    }

    public static class ConstantValue {
    	private Object value;
    	private boolean isConstant;
    	public ConstantValue() {
    		this.value = null;
    		this.isConstant = false;
    	}
    	public ConstantValue(Object v) {
    		this.value = v;
    		this.isConstant = true;
    	}
		public void setValue(Object value) {
			this.value = value;
		}
		public Object value() {
			return value;
		}
		public void setConstant(boolean isConstant) {
			this.isConstant = isConstant;
		}
		public boolean isConstant() {
			return isConstant;
		}
    }

    public x10.types.constants.ConstantValue constantValue() {
    	ConstantValue cv = constantRef.get();
    	if (cv == null || !cv.isConstant()) return null;
    	return x10.types.constants.ConstantValue.make(type().get(), cv.value());
    }
    
    public boolean isConstant() {
    	ConstantValue cv = constantRef.get();
    	if (cv == null) return false;
    	return cv.isConstant();
    }
    
    public Ref<ConstantValue> constantValueRef() {
    	return constantRef;
    }
 
    public Flags flags() {
        return flags;
    }
    
    public Ref<? extends Type> type() {
        return type;
    }

    public Name name() {
        return name;
    }

    public void setType(Ref<? extends Type> type) {
        this.type = type;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
    
    /** Destructive update of constant value. */
    public void setConstantValue(x10.types.constants.ConstantValue constantValue) {
//        if (! (constantValue == null) &&
//                ! (constantValue instanceof Boolean) &&
//                ! (constantValue instanceof Number) &&
//                ! (constantValue instanceof Character) &&
//                ! (constantValue instanceof String)) {
//            
//            throw new InternalCompilerError(
//            "Can only set constant value to a primitive or String.");
//        }

        this.constantRef.update(new ConstantValue(constantValue));
    }
    
    public void setNotConstant() {
    	this.constantRef.update(new ConstantValue());
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(Name name) {
        this.name = name;
    }
}
