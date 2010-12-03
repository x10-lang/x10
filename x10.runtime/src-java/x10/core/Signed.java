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

public class Signed {
    
    public static byte parseByte(String s, int radix) {
        try {
            return java.lang.Byte.parseByte(s, radix);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    public static byte parseByte(String s) {
        try {
            return java.lang.Byte.parseByte(s);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public static short parseShort(String s, int radix) {
        try {
            return java.lang.Short.parseShort(s, radix);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    public static short parseShort(String s) {
        try {
            return java.lang.Short.parseShort(s);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public static int parseInt(String s, int radix) {
        try {
            return java.lang.Integer.parseInt(s, radix);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    public static int parseInt(String s) {
        try {
            return java.lang.Integer.parseInt(s);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public static long parseLong(String s, int radix) {
        try {
            return java.lang.Long.parseLong(s, radix);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    public static long parseLong(String s) {
        try {
            return java.lang.Long.parseLong(s);
        } catch (java.lang.NumberFormatException e) {
            throw ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
}
