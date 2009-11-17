package x10.util;

import x10.util.HashMap;
import x10.util.GrowableRail;

public final class OptionsParser {

    public static final class Err extends Exception {
        global private val msg:String;
        public def this (m:String) { this.msg = m; }
        global public def toString() = "Commandline error: "+msg;
    }

    private val map : HashMap[String,String]!;
    private val set : HashMap[String,Boolean]!;
    private val filteredArgs : GrowableRail[String]!;

    public def this (args:Rail[String]!, flags:ValRail[Option]!, specs:ValRail[Option]!) throws Err {
        val map = new HashMap[String,String]();
        val set = new HashMap[String,Boolean]();
        val filteredArgs = new GrowableRail[String]();
        var offset:Int = 0;
        var ended:Boolean = false;
        for (var i:Int=0 ; i<args.length ; ++i) {
            val s = args(i);
            var recognised: Boolean = false;
            if (s.equals("--")) {
                ended = true;
                continue;
            }
            if (!ended) {
                if (flags!=null) for (flag in flags) {
                    if (recognised) break;
                    if (s.equals(flag.short_) || s.equals(flag.long_)) {
                        if (flag.short_!=null) set.put(flag.short_, true);
                        if (flag.long_!=null) set.put(flag.long_, true);
                        recognised = true;
                    }
                }
                if (specs!=null) for (spec in specs) {
                    if (recognised) break;
                    if (s.equals(spec.short_) || s.equals(spec.long_)) {
                        recognised = true;
                        ++i;
                        if (i>=args.length) throw new Err("Expected another arg after: \""+s+"\"");
                        val s2 = args(i);
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

    public def apply (key:String) = set.containsKey(key) || map.containsKey(key);

    public def apply (key:String, d:String) = map.getOrElse(key, d);
    public def apply (key:String, d:Int) throws Err {
        if (!map.containsKey(key)) return d;
        val v = map.getOrElse(key, "???");
        try {
            return Int.parseInt(v);
        } catch (e:NumberFormatException) {
            throw new Err("Expected Int, got: \""+v+"\"");
        }
    }
}

// vim: shiftwidth=8:tabstop=8:expandtab

