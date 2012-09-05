/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.runtime.impl.java;


public abstract class BoxUtils {

    // convert an X10 object to the equivalent Java object
    public static java.lang.Object unbox(java.lang.Object x10obj) {
        java.lang.Object javaobj = null;

        if (x10obj instanceof x10.core.Byte)
            javaobj = java.lang.Byte.valueOf(x10.core.Byte.$unbox((x10.core.Byte)x10obj));
        else if (x10obj instanceof x10.core.Short)
            javaobj = java.lang.Short.valueOf(x10.core.Short.$unbox((x10.core.Short)x10obj));
        else if (x10obj instanceof x10.core.Int)
            javaobj = java.lang.Integer.valueOf(x10.core.Int.$unbox((x10.core.Int)x10obj));
        else if (x10obj instanceof x10.core.Long)
            javaobj = java.lang.Long.valueOf(x10.core.Long.$unbox((x10.core.Long)x10obj));
        else if (x10obj instanceof x10.core.Float)
            javaobj = java.lang.Float.valueOf(x10.core.Float.$unbox((x10.core.Float)x10obj));
        else if (x10obj instanceof x10.core.Double)
            javaobj = java.lang.Double.valueOf(x10.core.Double.$unbox((x10.core.Double)x10obj));
        else if (x10obj instanceof x10.core.Char)
            javaobj = java.lang.Character.valueOf(x10.core.Char.$unbox((x10.core.Char)x10obj));
        else if (x10obj instanceof x10.core.Boolean)
            javaobj = java.lang.Boolean.valueOf(x10.core.Boolean.$unbox((x10.core.Boolean)x10obj));
        // FIXME unsigned types
        else if (x10obj instanceof x10.core.UByte)
            javaobj = java.lang.Byte.valueOf(x10.core.UByte.$unbox((x10.core.UByte)x10obj));
        else if (x10obj instanceof x10.core.UShort)
            javaobj = java.lang.Short.valueOf(x10.core.UShort.$unbox((x10.core.UShort)x10obj));
        else if (x10obj instanceof x10.core.UInt)
            javaobj = java.lang.Integer.valueOf(x10.core.UInt.$unbox((x10.core.UInt)x10obj));
        else if (x10obj instanceof x10.core.ULong)
            javaobj = java.lang.Long.valueOf(x10.core.ULong.$unbox((x10.core.ULong)x10obj));
        else
            javaobj = x10obj;

        return javaobj;
    }

    // convert an array of X10 objects to an array of the equivalent Java objects
    public static java.lang.Object[] unbox(java.lang.Object[] x10objs) {
        java.lang.Object[] javaobjs = new java.lang.Object[x10objs.length];

        for (int i = 0; i < x10objs.length; ++i) {
            javaobjs[i] = unbox(x10objs[i]);
        }

        return javaobjs;
    }

}
