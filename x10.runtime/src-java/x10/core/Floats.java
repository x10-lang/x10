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

public class Floats {
    public static byte toByte(float a) {
        int ia = (int)a;
        if (ia > Byte.MAX_VALUE) return Byte.MAX_VALUE;
        else if (ia < Byte.MIN_VALUE) return Byte.MIN_VALUE;
        else return (byte)ia;
    }
    
    public static byte toByte(double a) {
      int ia = (int)a;
      if (ia > Byte.MAX_VALUE) return Byte.MAX_VALUE;
      else if (ia < Byte.MIN_VALUE) return Byte.MIN_VALUE;
      else return (byte)ia;
  }
  
    public static short toShort(float a) {
        int ia = (int)a;
        if (ia > Short.MAX_VALUE) return Short.MAX_VALUE;
        else if (ia < Short.MIN_VALUE) return Short.MIN_VALUE;
        else return (short)ia;
    }
    
    public static short toShort(double a) {
        int ia = (int)a;
        if (ia > Short.MAX_VALUE) return Short.MAX_VALUE;
        else if (ia < Short.MIN_VALUE) return Short.MIN_VALUE;
        else return (short)ia;
    }
    
    public static byte toUByte(float a) {
        int ia = (int)a;
        if (ia > 0xff) return (byte)0xff;
        else if (ia < 0) return 0;
        else return (byte)ia;
    }
    
    public static byte toUByte(double a) {
        int ia = (int)a;
        if (ia > 0xff) return (byte)0xff;
        else if (ia < 0) return 0;
        else return (byte)ia;
    }
    
    public static short toUShort(float a) {
        int ia = (int)a;
        if (ia > 0xffff) return (short)0xffff;
        else if (ia < 0) return 0;
        else return (short)ia;
    }
    
    public static short toUShort(double a) {
        int ia = (int)a;
        if (ia > 0xffff) return (short)0xffff;
        else if (ia < 0) return 0;
        else return (short)ia;
    }

}
