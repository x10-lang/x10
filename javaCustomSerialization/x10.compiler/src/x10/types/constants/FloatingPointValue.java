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

import polyglot.types.TypeSystem;

/**
 * @author Bowen Alpern
 *
 */
public final class FloatingPointValue extends ConstantValue {
    
    public FloatingPointValue(TypeSystem ts, float f) {
        super(f, ts.Float());
    }
    
    public FloatingPointValue(TypeSystem ts, double d) {
        super(d, ts.Double());
    }

	@Override
	public Double value() {
		if (value instanceof Float)
			return (double) (float) (Float) value;
		return (Double) value;
	}
    
    public float asFloat() {
    	if (value instanceof Float)
    		return (float) (Float) value;
    	return (float) (double) (Double) value;
    }
    
    public double asDouble() {
    	if (value instanceof Float)
    		return (double) (float) (Float) value;
    	return (double) (Double) value;
    }
    
}
