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
import x10.util.GrowableIndexedMemoryChunk;

/**
 * @author Dave Cunningham
*/
public final class OptionsParser {

    public static final class Err extends Exception {
        private val msg:String;
        public def this (m:String) { this.msg = m; }
        public def this () {this.msg = null;}
         public def toString() = "Commandline error: "+msg;
    }

    private val map : HashMap[String,String];
    private val set : HashMap[String,Boolean];
    private val flags : Array[Option](1);
    private val specs : Array[Option](1);
    private val filteredArgs : GrowableIndexedMemoryChunk[String];

    public def this (args:Array[String](1), flags:Array[Option](1), specs:Array[Option](1)) { //throws Err {
        val map = new HashMap[String,String]();
        val set = new HashMap[String,Boolean]();
        val filteredArgs = new GrowableIndexedMemoryChunk[String]();
        var offset:Int = 0;
        var ended:Boolean = false;
        for (var i:Int=0 ; i<args.size; ++i) {
            val s = (args(i));
            var recognised: Boolean = false;
            if (s.equals("--")) {
                ended = true;
                continue;
            }
            if (s.length() <= 1 || s(0) != '-') { // too short or doesn't start with -
                filteredArgs.add(s);
                continue;
            }
            if (!ended) {
                if (s(1)!='-') {
                    // of the form -stuff

                    recognised = true; // set it to false if any of the letters aren't valid options
                    for ([index] in 1..(s.length()-1)) {
                        val char = s(index);
                        var char_recognised:Boolean = false;
                        if (flags!=null) for (flag in flags.values()) {
                            if (flag.short_ != null && char.equals(flag.short_(0))) {
                                char_recognised = true;
                            }
                        }
                        if (specs!=null && index==s.length()-1) for (spec in specs.values()) {
                            if (spec.short_ != null && char.equals(spec.short_(0))) {
                                char_recognised = true;
                            }
                        }
                        if (!char_recognised) {
                            recognised = false;
                            break;
                        }
                    }

                    if (recognised) {
                        for ([index] in 1..(s.length()-1)) {
                            val char = s(index);
                            if (flags!=null) for (flag in flags.values()) {
                                if (flag.short_!=null && char.equals(flag.short_(0))) {
                                    set.put("-"+flag.short_, true);
                                    if (flag.long_!=null) set.put(flag.long_, true);
                                }
                            }
                            if (specs!=null && index==s.length()-1) for (spec in specs.values()) {
                                if (spec.short_!=null && char.equals(spec.short_(0))) {
                                    ++i;
                                    if (i>=args.size) throw new Err("Expected another arg after: \""+s+"\"");
                                    val s2 = (args(i));
                                    map.put("-"+spec.short_, s2);
                                    if (spec.long_!=null) map.put(spec.long_, s2);
                                }
                            }
                        }
                    }

                } else {
                    // of the form --stuff, just compare with all the long_ fields in the options
                    if (flags!=null) for (flag in flags.values()) {
                        if (recognised) break;
                        if (s.equals(flag.long_)) {
                            if (flag.short_!=null) set.put("-"+flag.short_, true);
                            set.put(flag.long_, true);
                            recognised = true;
                        }
                    }
                    if (specs!=null) for (spec in specs.values()) {
                        if (recognised) break;
                        if (s.equals(spec.long_)) {
                            recognised = true;
                            ++i;
                            if (i>=args.size) throw new Err("Expected another arg after: \""+s+"\"");
                            val s2 = (args(i));
                            if (spec.short_!=null) map.put("-"+spec.short_, s2);
                            map.put(spec.long_, s2);
                        }
                    }
                }
            }
            if (!recognised) filteredArgs.add(s);
        }
        this.map = map;
        this.set = set;
        this.flags = flags;
        this.specs = specs;
        this.filteredArgs = filteredArgs;
    }

    public def filteredArgs() = filteredArgs.toArray();

    static def padding (p:Int) {
        // need a more efficient way of doing this:
        var r:String = new String();
        for ([i] in 1..p) r += " ";
        return r;
    }
        

    public def usage() {
        var r:String = new String();
        r += "Usage:";
        var shortWidth:Int = 0;
        for (opt in flags.values()) {
	    if (opt.short_ != null) {
                shortWidth = Math.max(shortWidth, opt.short_.length());
            }
        }
        for (opt in specs.values()) {
	    if (opt.short_ != null) {
                shortWidth = Math.max(shortWidth, opt.short_.length());
            }
        }
        var longWidth:Int = 0;
        for (opt in flags.values()) {
	    if (opt.long_ != null) {
                longWidth = Math.max(longWidth, opt.long_.length());
            }
        }
        for (opt in specs.values()) {
	    if (opt.long_ != null) {
                longWidth = Math.max(longWidth, opt.long_.length());
            }
        }
        for (opt in flags.values()) {
            // the 8 is to match the " <param>"
            val shortPadding = 8+shortWidth - opt.short_.length();
            val longPadding = longWidth - opt.long_.length();
            r += "\n    "+opt.short_+padding(shortPadding)
               + " ("+opt.long_+") "+padding(longPadding)+opt.description;
        }
        for (opt in specs.values()) {
            val shortPadding = shortWidth - opt.short_.length();
            val longPadding = longWidth - opt.long_.length();
            r += "\n    "+opt.short_+" <param>"+padding(shortPadding)
               + " ("+opt.long_+") "+padding(longPadding)+opt.description;
        }
        return r;
    }

    public operator this (key:String):Boolean = set.containsKey(key) || map.containsKey(key);

    public operator this (key:String, d:String):String = map.getOrElse(key, d);
    public operator this (key:String, d:UByte):UByte { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UByte.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UByte, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:Byte):Byte { // throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Byte.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Byte, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:UShort):UShort { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UShort.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UShort, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:Short):Short { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Short.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Short, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:UInt):UInt { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return UInt.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected UInt, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:Int):Int { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Int.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Int, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:ULong):ULong { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return ULong.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected ULong, got: \""+v+"\"");
        }
    }
    public operator this (key:String, d:Long):Long { //throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Long.parse(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Long, got: \""+v+"\"");
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
