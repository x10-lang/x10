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
import x10.compiler.Pinned;

@Pinned 
public class StringBuilder implements Builder[Any,String] {
     val buf: ArrayList[Char];

    public def this() {
        buf = new ArrayList[Char]();
    }

    /**
     * toString() returns the String being constructed by the StringBuilder
     * (same as calling @link{#result}).
     *
     * @return the String object being constructed by the StringBuilder
     */
    public def toString() {
      return result();
    }

    public def add(o:Any): StringBuilder {
        if (o == null)
            return addString("null");
        else
            return addString(o.toString());
    }

    public def insert(p:Int, o:Any): StringBuilder {
        if (o == null)
            return insertString(p, "null");
        else
            return insertString(p, o.toString());
    }

    public def add(x:Char):StringBuilder {
        buf.add(x);
        return this;
    }

    public def add(x:Boolean) = addString(x.toString());
    public def add(x:Byte) = addString(x.toString());
    public def add(x:Short) = addString(x.toString());
    public def add(x:Int) = addString(x.toString());
    public def add(x:Long) = addString(x.toString());
    public def add(x:Float) = addString(x.toString());
    public def add(x:Double) = addString(x.toString());
    public def add(x:String) = x == null ? addString("null") : addString(x);

    public def insert(p:Int, x:Boolean) = insertString(p, x.toString());
    public def insert(p:Int, x:Byte) = insertString(p, x.toString());
    public def insert(p:Int, x:Char) = insertString(p, x.toString());
    public def insert(p:Int, x:Short) = insertString(p, x.toString());
    public def insert(p:Int, x:Int) = insertString(p, x.toString());
    public def insert(p:Int, x:Long) = insertString(p, x.toString());
    public def insert(p:Int, x:Float) = insertString(p, x.toString());
    public def insert(p:Int, x:Double) = insertString(p, x.toString());
    public def insert(p:Int, x:String) = x == null ? insertString(p, "null") : insertString(p, x);

    public def addString(s:String/*{self!=null}*/): StringBuilder {
        for (var i: int = 0; i < s.length(); i++) {
            val ch = s(i);
            buf.add(ch);
        }
        return this;
    }

    public def insertString(pos:Int, s: String/*{self!=null}*/): StringBuilder {
        var loc:Int = pos;
        if (s.length() == 0)
            return this;

        if (loc > buf.size()) { // treat it as append if postion is beyond the tail.
            return addString(s);
        }

        if (loc < 0)    // Ensure loc is a valid index.
            loc = 0;

        for (var i: int = 0; i < s.length(); i++) {
            val ch = s(i);
            buf(loc+i)= ch;
        }
        return this;
    }

    public def length() {
        return buf.size();
    }

    public def result():String {
        val rail = buf.toRail();
        return new String(rail, 0, rail.size);
    }
}
