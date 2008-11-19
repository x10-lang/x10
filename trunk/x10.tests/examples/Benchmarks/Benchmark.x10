import harness.x10Test;

import x10.compiler.Native;
import x10.compiler.NativeRep;

import x10.io.StringWriter;
import x10.io.Printer;

abstract class Benchmark extends x10Test {

    abstract def once(): double;
    abstract def expected(): double;
    abstract def operations(): double;

    def now() = (System.nanoTime() to double) * 1e-9;
    def pr(s:String) = System.out.println(s);

    var host:String = "";
    var kind:String = "";
    var reps:int = 1;
    var summary:boolean = false;
    var referenceOps:double = 0.0;
    var aboveOps:double = 0.0;
    var fastest: String = "";
    var above: String = "";

    def this(args:Rail[String]) {
        if (args.length==2) {
            this.host = args(0);
            this.kind = args(1);
            this.reps = 10;
        } else if (args.length==1) {
            this.host = args(0);
            this.reps = 0;
            this.summary = true;
        }
    }

    // workaround for XTENLANG-177
    def sprintf(fmt:String, x0:Object, x1:Object) {
        val w = new StringWriter();
        val p = new Printer(w);
        p.printf(fmt, x0, x1);
        return w.toString();
    }

    // workaround for XTENLANG-177
    def sprintf(fmt:String, x0:Object) {
        val w = new StringWriter();
        val p = new Printer(w);
        p.printf(fmt, x0);
        return w.toString();
    }

    def fmt(x:double, units:String) {
        if (x<1e6)      return sprintf("%.3g k%s", x/1e3, units);
        else if (x<1e9) return sprintf("%.3g M%s", x/1e6, units);
        else            return sprintf("%.3g G%s", x/1e9, units);
    }

    def reference(host:String, kind:String, ops:double) {
        if (summary) {
            if (host==this.host) {
                if (referenceOps==0.0) {
                    fastest = kind;
                    referenceOps = ops;
                }
                val relative = referenceOps / ops;
                System.out.printf("%-20s %s", kind, fmt(ops,"op/s"));
                if (kind!=fastest)
                    System.out.printf(" =%s/%.1f", fastest, referenceOps/ops);
                if (aboveOps!=0.0 && above!=fastest)
                    System.out.printf(" =%s/%.1f", above, aboveOps/ops);
                aboveOps = ops;
                above = kind;
                System.out.println();
            }
        } else {
            if (host==this.host && kind==this.kind)
                referenceOps = ops;
        }
    }

    @Native("java", "java.lang.System.gc()")
    public static native def gc(): void;

    public def run():boolean {

        var minTime:double = double.POSITIVE_INFINITY;
        var maxGC:double = 0.0;
        var first: boolean = true;

        // do timing
        for (var i:int=0; i<reps; i++) {

            // run benchmark once and time it
            val start = now();
            val result = once();
            val time = now() - start;
            if (time<minTime) {
                minTime = time;
                i = 0;
            }

            // do gc and time it
            val startGC = now();
            gc();
            gc();
            val timeGC = now() - startGC;
            if (timeGC>maxGC)
                maxGC = timeGC;

            // print timings
            System.out.printf("time %.3f, min %.3f; ", time, minTime);
            System.out.printf("gc %.3f, max %.3f\n", timeGC, maxGC);

            // check for correctness first time through
            if (first && result!=expected()) {
                pr("got " + result + "; expected " + expected());
                return false;
            }
            first = false;
        }

        // timing info
        if (reps>0) {

            // current info
            val currentOps = operations() / minTime;
            pr(host + " " + kind + " " + sprintf("%.5e",currentOps));
            pr("current: " + fmt(currentOps, "op/s"));
            
            // relative to reference
            if (referenceOps!=0) {
                pr("reference: " + fmt(referenceOps, "op/s"));
                val relative = currentOps / referenceOps;
                val msg =
                    relative>1.1? ": SIGNIFICANT IMPROVEMENT" :
                    relative<0.9? ": SIGNIFICANT DEGRADATION" :
                    "";
                pr("current/reference: " + sprintf("%.2f",relative) + msg);
            } else
                pr("no reference data available");

        }

        return true;
    }

}
