/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import x10.rtt.Equality;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

// Base class of all X10 value objects -- should be generated, but we need this class to get Box to compile.
public class Value {
    
    public static class RTT extends RuntimeType<Value> {
    	public static final RTT it = new RTT();

    	public RTT() {
            super(Value.class);
        }

        @Override
        public boolean instanceof$(Object o) {
            return o instanceof Value;
        }
    }
    
    public static class BoxedValue extends Box<Value> {
		BoxedValue(Value v) {
			super(Value.RTT.it, v);
		}
	}
    
	public Ref box$() {
		return new BoxedValue(this);
	}
	
    public boolean equals(Object o) {
        if (o instanceof Value)
            return this.equals((Value) o);
        return false;
    }
	
    public boolean equals(Ref o) {
        return false;
    }

    public boolean equals(Value o) {
        return structEquals(o);
    }
    
    public int hashCode() {
        return structHash();
    }
	
    public final int structHash() {
        Class<?> c = this.getClass();
        Object o = this;
        int hash = 17;
        try {
            while (c != null) {
                Field[] fs = c.getDeclaredFields();
                for (int i = fs.length - 1; i >= 0; i--) {
                    Field f = fs[i];
                    if (Modifier.isStatic(f.getModifiers()))
                        continue;
                    if (Type.class.isAssignableFrom(f.getType()))
                        continue;
                    f.setAccessible(true);
                    if (f.getType().isPrimitive()) {
                        hash = hash * 37 + f.get(o).hashCode();
                    }
                    else if (f.getType().isArray()) {
                        java.lang.Object a = f.get(o);
                        int len = Array.getLength(a);
                        for (int j = 0; j < len; j++) {
                            hash = hash * 37 + Array.get(a, j).hashCode();
                        }
                    }
                    else {
                        // I assume here that value types are immutable
                        // and can thus not contain mutually recursive
                        // structures.  If that is wrong, we would have to do
                        // more work here to avoid dying with a StackOverflow.
                        hash = hash * 37 + f.get(o).hashCode();
                    }
                }
                c = c.getSuperclass();
            }
        }
        catch (IllegalAccessException iae) {
            throw new Error(iae); // fatal, should never happen
        }
        return hash;
    }

    public final boolean structEquals(Object o) {
        if (o == null)
            return false;
        if (o == this)
        	return true;
        Class<?> c = this.getClass();
        Object o1 = this;
        Object o2 = o;
        if (c != o2.getClass())
            return false;
        try {
            while (c != null) {
                Field[] fs = c.getDeclaredFields();
                for (int i = fs.length - 1; i >= 0; i--) {
                    Field f = fs[i];
                    if (Modifier.isStatic(f.getModifiers()))
                        continue;
                    f.setAccessible(true);
                    Object a1 = f.get(o1);
                    Object a2 = f.get(o2);
					if (f.getType().isPrimitive()) {
						if (!Equality.equalsequals(a1, a2))
                            return false;
                    }
                    else if (f.getType().isArray()) {
                        int len = Array.getLength(a1);
                        if (len != Array.getLength(a2))
                            return false;
                        for (int j = 0; j < len; j++)
                        	if (!Equality.equalsequals(Array.get(a1, j), Array.get(a2, j)))
                                return false;
                    }
                    else {
                        // I assume here that value types are immutable
                        // and can thus not contain mutually recursive
                        // structures.  If that is wrong, we would have to do
                        // more work here to avoid dying with a StackOverflow.
                        if (!Equality.equalsequals(a1, a2))
                            return false;
                    }
                }
                c = c.getSuperclass();
            }
        }
        catch (IllegalAccessException iae) {
            throw new Error(iae); // fatal, should never happen
        }
        return true;
    }
}
