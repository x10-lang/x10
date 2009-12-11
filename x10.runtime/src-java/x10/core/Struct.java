package x10.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import x10.types.Equality;
import x10.types.RuntimeType;
import x10.types.Type;

// Base class for all X10 structs
public abstract class Struct {

    public static class RTT extends RuntimeType<Struct> {
    	public static final RTT it = new RTT();

    	public RTT() {
            super(Struct.class);
        }

        @Override
        public boolean instanceof$(Object o) {
	    return o instanceof Struct;
        }

    }

    public Struct() {}

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
