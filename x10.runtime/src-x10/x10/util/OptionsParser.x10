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

import x10.util.HashMap;
import x10.util.GrowableRail;

/**
 * @author Dave Cunningham
*/
public final class OptionsParser {

    public static final class Err extends Exception {
        private val msg:String;
        public def this (m:String) { this.msg = m; }
         public def toString() = "Commandline error: "+msg;
    }

    private val map : HashMap[String,String];
    private val set : HashMap[String,Boolean];
    private val filteredArgs : GrowableRail[String];

    public def this (args:Array[String](1), flags:Array[Option](1), specs:Array[Option](1)) { //throws Err {
        val map = new HashMap[String,String]();
        val set = new HashMap[String,Boolean]();
        val filteredArgs = new GrowableRail[String]();
        var offset:Int = 0;
        var ended:Boolean = false;
        for (var i:Int=0 ; i<args.size; ++i) {
            val s = (args(i));
            var recognised: Boolean = false;
            if (s.equals("--")) {
                ended = true;
                continue;
            }
            if (!ended) {
                if (flags!=null) for (flag in flags.values()) {
                    if (recognised) break;
                    if (s.equals(flag.short_) || s.equals(flag.long_)) {
                        if (flag.short_!=null) set.put(flag.short_, true);
                        if (flag.long_!=null) set.put(flag.long_, true);
                        recognised = true;
                    }
                }
                if (specs!=null) for (spec in specs.values()) {
                    if (recognised) break;
                    if (s.equals(spec.short_) || s.equals(spec.long_)) {
                        recognised = true;
                        ++i;
                        if (i>=args.size) throw new Err("Expected another arg after: \""+s+"\"");
                        val s2 = (args(i));
                        if (spec.short_!=null) map.put(spec.short_, s2);
                        if (spec.long_!=null) map.put(spec.long_, s2);
                    }
                }
            }
            if (!recognised) filteredArgs.add(s);
        }
        this.map = map;
        this.set = set;
        this.filteredArgs = filteredArgs;
    }

    public def filteredArgs() = filteredArgs.toRail();

    public operator this (key:String):Boolean = set.containsKey(key) || map.containsKey(key);

    public operator this (key:String, d:String):String = map.getOrElse(key, d);
    /* Uncomment on resolution of XTENLANG-1413
    public operator this (key:String, d:UByte) { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UByte.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UByte, got: \""+v+"\"");
        }
    }
    */
    public operator this (key:String, d:Byte):Byte { // throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Byte.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Byte, got: \""+v+"\"");
        }
    }
    /* Uncomment on resolution of XTENLANG-1413
    public operator this (key:String, d:UShort) { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UShort.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UShort, got: \""+v+"\"");
        }
    }
    */
    public operator this (key:String, d:Short):Short { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Short.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Short, got: \""+v+"\"");
        }
    }
    /* Uncomment on resolution of XTENLANG-1413
    public operator this (key:String, d:UInt) { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UInt.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UInt, got: \""+v+"\"");
        }
    }
    */
    public operator this (key:String, d:Int):Int { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Int.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Long, got: \""+v+"\"");
        }
    }
    /* Uncomment on resolution of XTENLANG-1413
    public operator this (key:String, d:ULong) { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return ULong.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected ULong, got: \""+v+"\"");
        }
    }
    */
    public operator this (key:String, d:Long):Long { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Long.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Int, got: \""+v+"\"");
        }
    }


    public operator this (key:String, d:Double):Double { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Double.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Double, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:Float):Float { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Float.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Float, got: \""+v+"\"");
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
