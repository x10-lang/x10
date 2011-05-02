/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

public abstract class Signed {
    
    public static java.lang.String toString(byte a, int radix) {
        if (a >= 0) {
            return Integer.toString(a, radix);
        } else {
            int b = (0x80000000 - a) & 0x7FFFFFFF;
            return "-" + Integer.toString(b, radix);
        }
    }

    public static java.lang.String toString(short a, int radix) {
        if (a >= 0) {
            return Integer.toString(a, radix);
        } else {
            int b = (0x80000000 - a) & 0x7FFFFFFF;
            return "-" + Integer.toString(b, radix);
        }
    }

    // TODO remove the followings
//    @Deprecated
//    public static byte parseByte(java.lang.String s, int radix) {
//        try {
//            return java.lang.Byte.parseByte(s, radix);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//
//    @Deprecated
//    public static byte parseByte(java.lang.String s) {
//        try {
//            return java.lang.Byte.parseByte(s);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//    
//    @Deprecated
//    public static short parseShort(java.lang.String s, int radix) {
//        try {
//            return java.lang.Short.parseShort(s, radix);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//
//    @Deprecated
//    public static short parseShort(java.lang.String s) {
//        try {
//            return java.lang.Short.parseShort(s);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//    
//    @Deprecated
//    public static int parseInt(java.lang.String s, int radix) {
//        try {
//            return java.lang.Integer.parseInt(s, radix);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//
//    @Deprecated
//    public static int parseInt(java.lang.String s) {
//        try {
//            return java.lang.Integer.parseInt(s);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//    
//    @Deprecated
//    public static long parseLong(java.lang.String s, int radix) {
//        try {
//            return java.lang.Long.parseLong(s, radix);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
//
//    @Deprecated
//    public static long parseLong(java.lang.String s) {
//        try {
//            return java.lang.Long.parseLong(s);
//        } catch (java.lang.NumberFormatException e) {
//            throw ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }
    
}
