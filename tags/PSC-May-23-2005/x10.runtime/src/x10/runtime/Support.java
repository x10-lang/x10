/*
 * Created on Feb 17, 2005
 */
package x10.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import x10.lang.Indexable;
import x10.lang.ValueType;

/**
 * Class with some support functions that don't fit anywhere else.
 *
 * @author Christian Grothoff
 */
public class Support {

    /**
     * Do not instantiate. 
     */
    private Support() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Implementation of "==" for value types.
     * @param o1
     * @param o2
     * @return true iff the values are value-equals (all fields have
     *    the same value)
     */
    public static boolean equalsequals(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if ( (o1 == null) || (o2 == null))
            return false;
        Class c = o1.getClass();
        if ( (o1 instanceof Indexable)) {
            Indexable i1 = (Indexable) o1;
            Indexable i2 = (Indexable) o2;
            if (! (i1.isValue() && i2.isValue()))
                return false;
            return i1.valueEquals(i2);
        }
        if (c != o2.getClass())
            return false;
        if ( !(o1 instanceof ValueType) )
            return false;        
        try {
            while (c != null) {
                Field[] fs = c.getDeclaredFields();
                for (int i=fs.length-1;i>=0;i--) {
                    Field f = fs[i];
                    if (Modifier.isStatic(f.getModifiers()))
                        continue;
                    f.setAccessible(true);
                    if (f.getType().isPrimitive()) {
                        if (! f.get(o1).equals(f.get(o2)))
                            return false;
                    } else {
                        // I assume here that value types are immutable
                        // and can thus not contain mutually recursive
                        // structures.  If that is wrong, we would have to do
                        // more work here to avoid dying with a StackOverflow.
                        if (! equalsequals(f.get(o1), f.get(o2))) 
                            return false;
                    }                    
                }
                c = c.getSuperclass();
                if ( (c == java.lang.Object.class) ||
                     (c == x10.lang.Object.class) )
                    break; // otherwise we get problems with fields like 'place' in X10Object
            } 
        } catch (IllegalAccessException iae) {
            throw new Error(iae);  // fatal, should never happen
        }
        return true;
    }

}
