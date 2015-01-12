/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.runtime.impl.java;


public abstract class StringUtils {

    public static String format(String format, Object[] args) {
        Object[] copy = new Object[args.length];

        // rebox x10.core.Int objects into java.lang.Integers
        for (int i = 0; i < args.length; ++i) {
            Object oldarg = args[i];
            Object newarg = null;

            if (oldarg instanceof x10.core.Byte)
                newarg = java.lang.Byte.valueOf(x10.core.Byte.$unbox((x10.core.Byte)oldarg));
            else if (oldarg instanceof x10.core.Short)
                newarg = java.lang.Short.valueOf(x10.core.Short.$unbox((x10.core.Short)oldarg));
            else if (oldarg instanceof x10.core.Int)
                newarg = java.lang.Integer.valueOf(x10.core.Int.$unbox((x10.core.Int)oldarg));
            else if (oldarg instanceof x10.core.Long)
                newarg = java.lang.Long.valueOf(x10.core.Long.$unbox((x10.core.Long)oldarg));
            else if (oldarg instanceof x10.core.Float)
                newarg = java.lang.Float.valueOf(x10.core.Float.$unbox((x10.core.Float)oldarg));
            else if (oldarg instanceof x10.core.Double)
                newarg = java.lang.Double.valueOf(x10.core.Double.$unbox((x10.core.Double)oldarg));
            else if (oldarg instanceof x10.core.Char)
                newarg = java.lang.Character.valueOf(x10.core.Char.$unbox((x10.core.Char)oldarg));
            else if (oldarg instanceof x10.core.Boolean)
                newarg = java.lang.Boolean.valueOf(x10.core.Boolean.$unbox((x10.core.Boolean)oldarg));
            // FIXME unsigned types
            else if (oldarg instanceof x10.core.UByte)
                newarg = java.lang.Byte.valueOf(x10.core.UByte.$unbox((x10.core.UByte)oldarg));
            else if (oldarg instanceof x10.core.UShort)
                newarg = java.lang.Short.valueOf(x10.core.UShort.$unbox((x10.core.UShort)oldarg));
            else if (oldarg instanceof x10.core.UInt)
                newarg = java.lang.Integer.valueOf(x10.core.UInt.$unbox((x10.core.UInt)oldarg));
            else if (oldarg instanceof x10.core.ULong)
                newarg = java.lang.Long.valueOf(x10.core.ULong.$unbox((x10.core.ULong)oldarg));
            else
                newarg = oldarg;

            copy[i] = newarg;
        }

        return String.format(format, copy);
    }

}
