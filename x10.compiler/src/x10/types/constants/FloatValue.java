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

import polyglot.ast.FloatLit;
import polyglot.ast.NodeFactory;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * A class to represent a constant of type Float.
 * Intentionally not overloading DoubleValue to avoid
 * rounding/precision differences between Float and Double.
 */
public final class FloatValue extends ConstantValue {
    private final float val;
    
    FloatValue(float f) {
        val = f;
    }
    
    public float value() {
        return val;
    }
    
    public Float toJavaObject() {
        return Float.valueOf(val);
    }
    
    public FloatLit toLit(NodeFactory nf, TypeSystem ts, Position pos) {
        return (FloatLit)nf.FloatLit(pos, FloatLit.FLOAT, val).type(ts.Float());
    }

    public FloatLit toUntypedLit(NodeFactory nf, Position pos) {
        return (FloatLit)nf.FloatLit(pos, FloatLit.FLOAT, val);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof FloatValue) {
            return ((FloatValue) that).val == val;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Float.valueOf(val).hashCode();
    }
    
    @Override
    public String toString() {
        return Float.toString(val);
    }
    
    @Override
    public long integralValue() {
        return (long)val;
    }
   
    @Override
    public double doubleValue() {
        return (double)val;
    }
    
    @Override
    public float floatValue() {
        return (float)val;
    }
    
}