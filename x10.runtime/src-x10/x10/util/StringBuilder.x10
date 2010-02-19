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

package x10.util;

import x10.compiler.Native;

public class StringBuilder implements Builder[Object,String] {
     val buf: ValRailBuilder[Char]!;

    public def this() {
        buf = new ValRailBuilder[Char]();
    }

    /**
     * When invoked by an activity at the same place as the StringBuider object,
     * toString() returns the String being constructed by the StringBuilder 
     * (same as calling @link{#result}). When invoked by an activity at a different
     * place, an UnsupportedOperationException is raised.
     *
     * @return the String object being constructed by the StringBuilder
     */
    public global safe def toString() {
      if (!at(here)) throw new UnsupportedOperationException();
      return (this as StringBuilder!).result();
    }

    public def add(o:Object): StringBuilder {
        if (o == null)
            return addString("null");
        else
            return addString((o as Object!).toString());
    }

    public def add(x:Boolean) = addString(x.toString());
    public def add(x:Byte) = addString(x.toString());
    public def add(x:Char) = addString(x.toString());
    public def add(x:Short) = addString(x.toString());
    public def add(x:Int) = addString(x.toString());
    public def add(x:Long) = addString(x.toString());
    public def add(x:Float) = addString(x.toString());
    public def add(x:Double) = addString(x.toString());
    
    protected def addString(s: String): StringBuilder {
        for (var i: int = 0; i < s.length(); i++) {
            val ch = s(i);
            buf.add(ch);
        }
        // BROKEN code gen
        /*
        for (ch in s.chars()) {
            buf.add(ch);
        }
        */
        return this;
    }
    
    public def length() {
        return buf.length();
    }
    
    @Native("java", "new String(#1.getCharArray())")
    @Native("c++", "x10aux::vrc_to_string(#1)")
    private static safe global native def makeString(ValRail[Char]): String;

    public def result() = makeString(buf.result());
    
    
}
