/*
 * Copyright (c) 1996 The Regents of the University of California.
 * All rights reserved.
 *
 * Authors: William G. Griswold (wgg@cs.ucsd.edu) and Paul S. Phillips
 * (paulp@go2net.com)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *      This product includes software developed by the University of
 *      California, San Diego and its contributors.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.io.Console;
import x10.io.File;
import x10.io.FileWriter;
import x10.io.Printer;
import x10.util.Random;

class Timer {
    private var base_time: Long;
    private var elapsed_time: Long;

    private static val UNIT = 1000000L;

    def this() {
        clear();
    }

    def mark(): void {
        base_time = System.nanoTime();
    }

    def clear(): void {
        elapsed_time = 0;
    }

    def record(): void {
        elapsed_time += (System.nanoTime() - base_time);
    }

    def elapsed(): Float {
        return (elapsed_time as Float) / UNIT;
    }

    public def report(pstream: Printer) {
        elapsed_seconds: Float = elapsed();
        pstream.println("Time " + elapsed_seconds + " msec");
    }
}

// valid to X10-Java only
class GCTest {
    static class Counter {
        var objs_collected: Int;
        def add() {
            ++objs_collected;
        }
    }
    
    @NativeRep("java", "java.lang.Runtime", null, null)
    static class NativeRuntime {
	@Native("java", "java.lang.Runtime.getRuntime()")
        public static def getRuntime() = new NativeRuntime();
        
	@Native("java", "#0.gc()")
	public def gc() { }
        
	@Native("java", "#0.totalMemory()")
	public def totalMemory() = 0L;
        
	@Native("java", "#0.freeMemory()")
	public def freeMemory() = 0L;
        
	@Native("java", "#0.runFinalization()")
	public def runFinalization() { }
    }

    // WGG - pick these sizes carefully: will kill some systems
    static val GC_POINTERS = 200;
    static val GC_OBJECTS = 2000;
    static val MAX_OBJ_SIZE = 4000;
    val objects = Rail.make[GCTestElem](GC_POINTERS);
    val counter: Counter! = new Counter();
    var seed1: Long  = 121393;  // WGG - no, I didn't look these up in Knuth
    var seed2: Long  = 196418;
    var total_size: Long  = 0;
    var freememory: Long, totalmemory: Long;
    var newfreememory: Long, newtotalmemory: Long, recoveredmemory: Long;
    var fullGCtimer: Timer!;
    var incrGCtimer: Timer!;
    
    public def this() {}
    
    // WGG - this was Math.random(), but we need something a little less random
    //  and system-specific in order to achieve cross-platform repeatability.
    def fiborandom(): Long = {
        var sum: Long = seed1 + seed2;
        if (sum < 0) sum *= -1;

        seed1 = seed2;
        seed2 = sum;
	return sum;
    }
    
    public def start(pstream: Printer): void {
        var size: Int, which: Int;
        val RT = NativeRuntime.getRuntime();

        // Test the cost of a full mark-and-sweep (at least for Sun Java)
        totalmemory = RT.totalMemory();
        freememory = RT.freeMemory();
        fullGCtimer = new Timer();
        fullGCtimer.mark();
        RT.gc();	// may not necessarily perform GC
        RT.runFinalization();  // seems only fair
        fullGCtimer.record();
        newtotalmemory = RT.totalMemory();
        newfreememory = RT.freeMemory();
        recoveredmemory = (newfreememory - freememory) - (newtotalmemory - totalmemory);

        // Test the cost of a incremental GC (at least for Sun Java)
        incrGCtimer = new Timer();
        incrGCtimer.mark();

        for (var i: Int = 0; i < GC_OBJECTS; i++) {
    	    size = (fiborandom() % MAX_OBJ_SIZE) as Int;
    	    total_size += size;
    	    which = ((fiborandom() % GC_POINTERS) as Int) + 1; // WGG - no 0 sized objects
    	    objects(which) = new GCTestElem(counter, size);
        }

        incrGCtimer.record();
    }

    public def report(pstream: Printer) {
	pstream.println("System.gc() w/ " + freememory + "B memory avail of " + totalmemory + "B total");
	StringHelper.bmfill(pstream, "GC'd " + recoveredmemory + "B, leaving " + newfreememory + "B of " + newtotalmemory + "B total: ", fullGCtimer.elapsed());
	StringHelper.bmfill(pstream, GC_OBJECTS + " obj rand. new'd/assigned (avg " + (total_size / GC_OBJECTS) + "B), " + counter.objs_collected + " GC'd: ", incrGCtimer.elapsed());
    }
}

class GCTestElem {
    val collect: Rail[Byte];
    val counter: GCTest.Counter!;

    public def this(counter: GCTest.Counter!, size: Int) {
        this.counter = counter;
        collect = Rail.make[Byte](size);
    }

    protected def finalize() {
        counter.add();
    }
}

class LoopTest {
    static val ITERATIONS = 1000000;
    var t: Timer!;

    public def start(): void {
        t = new Timer();
        t.mark();
        for (var i: Int = 0; i < ITERATIONS; i++)
            ;
        t.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, "Empty loop iterated " + ITERATIONS + " times: ", t.elapsed());
    }
}

class ArrayTest {
    static val ARRAY_INTS = 1000000;
    val arr = Rail.make[Int](ARRAY_INTS);
    var t: Timer!;

    public def start(): void {
        t = new Timer();
        
        t.mark();
        for (var i: Int = 0; i < ARRAY_INTS; i++) {
            arr(i) = i;
        }
        t.record();
    }

    public def report(pstream: Printer) {
	StringHelper.bmfill(pstream,"Assigned to " + ARRAY_INTS + " array ints: ", t.elapsed());
    }
}

class FieldTest {
    static val FIELD_ACCESSES = 1000000;
    val fte: FieldTestElem! = new FieldTestElem();
    var t: Timer!;

    public def start(): void {
        var temp: Int;

        t = new Timer();

        t.mark();
        for (var i: Int = 0; i < FIELD_ACCESSES; i++) {
            temp = fte.data;
        }
        t.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, FIELD_ACCESSES + " object int field accesses: ", t.elapsed());
    }
}

class FieldTestElem {
    var data: Int = 1;
}

class ArithmeticTest {
    static val ADDS = 1000000;
    static val MULTS = 1000000;
    static val FADDS = 1000000;
    static val FMULTS = 1000000;
    var intaddtimer: Timer!, intmulttimer: Timer!;
    var doubleaddtimer: Timer!, doublemulttimer: Timer!;
    var sum: Int = 0, prod: Int = 1;
    var fsum: Double = 0.0, fprod: Double = 1.0;
    val realnumber = new Random().nextDouble() * 43213.5752 + 1; // WGG - don't use int i

    // WGG - note that there are extra adds hidden in the loop counter
    public def start(): void {
        intaddtimer = new Timer();
        intmulttimer = new Timer();
        doubleaddtimer = new Timer();
        doublemulttimer = new Timer();

        intaddtimer.mark();
        for (var i: Int = 0; i < ADDS; i++) {
            sum += i;
        }
        intaddtimer.record();

        intmulttimer.mark();
        for (var i: Int = 1; i <= MULTS; i++) {
            prod *= i;
        }
        intmulttimer.record();

        doubleaddtimer.mark();
        for (var i: Int = 0; i < FADDS; i++) {
            fsum += realnumber;
        }
        doubleaddtimer.record();

        doublemulttimer.mark();
        for (var i: Int = 1; i <= FMULTS; i++) {
            fprod *= realnumber;
        }
        doublemulttimer.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, "Added " + ADDS + " ints in loop: ", intaddtimer.elapsed());
	StringHelper.bmfill(pstream, "Multipled " + MULTS + " ints in loop: ", intmulttimer.elapsed());
	StringHelper.bmfill(pstream, "Added " + ADDS + " doubles in loop: ", doubleaddtimer.elapsed());
	StringHelper.bmfill(pstream, "Multipled " + MULTS + " doubles in loop: ", doublemulttimer.elapsed());
    }
}

class MethodTest {
    static val METHOD_CALLS = 1000000;
    var sametimer: Timer!, othertimer: Timer!;
    var mt: MethodTest!;

    public def start(): void {
        sametimer = new Timer();
        othertimer = new Timer();
        mt = new MethodTest();

        sametimer.mark();
        for (var i: Int = 0; i < METHOD_CALLS; i++) {
            empty();
        }
        sametimer.record();

        othertimer.mark();
        for (var i: Int = 0; i < METHOD_CALLS; i++) {
            mt.empty();
        }
        othertimer.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, METHOD_CALLS + " method calls in same object: ", sametimer.elapsed());
	StringHelper.bmfill(pstream, METHOD_CALLS + " method calls in other object: ", othertimer.elapsed());
    }

    public def empty() {
    }
}

class ExceptionTest {
    static val THROWS = 1000000;
    var t: Timer!;
    var exc: MyException;

    public def start(): void {
        t = new Timer();
        exc = new MyException();

        t.mark();
        for (var i: Int = 0; i < THROWS; i++) {
            try {
                throw exc;
            } catch (e:MyException) {
            }
        }
        t.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, "Threw and caught " + THROWS + " exceptions: ", t.elapsed());
    }
}

class MyException extends Exception {
}

// valid to X10-Java only
class ThreadTest {
    
    @NativeRep("java", "java.lang.Thread", null, null)
    final static class Thread {
	@Native("java", "java.lang.Thread.yield()")
	public static def yield(): void { }
    }
    
    static val THREADCNT = 3;
    static val SWITCHES = 10000;
    var t: Timer!;
    var i: Int;

    public def start(): void {
        t = new Timer();

        t.mark();
        finish {
            for (var i: Int = 0; i < THREADCNT; i++) {
                async {
                    for (var j: Int = 0; j < SWITCHES; j++) {
                        try {
                            Thread.yield();
                        } catch (e:Exception) {
                        }
                    }
                }
            }
        }
        t.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream,THREADCNT + " threads, switched " + SWITCHES + " times each: ", t.elapsed());
    }
}

class IOTest {
    var bytetimer: Timer!, blocktimer: Timer!;
    static val BUFSIZE = 100000;
    var buffer: Rail[Byte]!;
    var out: FileWriter;

    public def start(pstream: Printer): void {
        bytetimer = new Timer();
        blocktimer = new Timer();

        try {
            out = new File("tmpfile").openWrite();
        } catch (e: Exception) {
            pstream.println("Could not create \"tmpfile\" for IO benchmark");
            return;
        }

        bytetimer.mark();
        for (var i: Int = 0; i < BUFSIZE; i++) {
            try {
                out.write(0);
            } catch (e: Exception) {
            }
        }
        bytetimer.record();

        buffer = Rail.make[Byte](BUFSIZE);
        blocktimer.mark();
        try {
            out.write(buffer);
        } catch (e: Exception) {
        }
        blocktimer.record();
    }

    public def report(pstream: Printer): void {
	StringHelper.bmfill(pstream, BUFSIZE + " writes of 1 byte: ", bytetimer.elapsed());
	StringHelper.bmfill(pstream, "1 write of " + BUFSIZE + " bytes: ", blocktimer.elapsed());
    }
}

class StringHelper {

    public static def filler(amount: Int, chr: String): String {
        var pad: String = "";
        for (var i: Int = 1; i <= amount; i++)
            pad += chr;
        return pad;
    }

    public static def fill(str1: String, str2: String, outto: Int, chr: String): String {
        val padamount: Int = outto - (str1.length() + str2.length());
        return str1 + filler(padamount, chr) + str2;
    }

    public static def floatfill(num: Float, totalplaces: Int): String {
        var numstr: String = num + "";
        var numlen: Int = numstr.length();
        var places: Int = numlen - (numstr.indexOf('.') + 1);
        return fill(numstr, "", numlen + (totalplaces - places), "0");
    }

    public static def bmfill(ps: Printer, str1: String, str2: String): void {
        ps.println(fill(str1, str2, 64, " "));
    }

    public static def bmfill(ps: Printer, str1: String, num: Float): void {
        ps.println(fill(str1, floatfill(num, 3), 64, " "));
    }

} // end class StringHelper


class Benchmark {
    var next: Int;
    var pstream: Printer;
    var cumtimer: Timer!;
    var skipgc: Boolean;
    var mode: String;
    var optflags: String;
    static val version = "1.0";

    def this(thestream: Printer, SkipGc: Boolean, OptFlags: String) {
        cumtimer = new Timer();
        cumtimer.mark();
        next = -1; // preincrements to 0
        pstream = thestream;
        skipgc = SkipGc;
        mode = (pstream == x10.io.Console.OUT) ? "application" : "applet";
        optflags = (OptFlags != null) ? OptFlags : "unspecified";
    }

    public static def main(args: Rail[String]!): void {
        val bm = new Benchmark(x10.io.Console.OUT, false, "unspecified");

        if (args.length == 0)
            bm.runbenchmarks();
        else {
            for (var i: Int = 0; i < args.length; i++)
                bm.runTest(args(i));
        }
    }

    public def runbenchmarks(): void {
        while (nextTest())
            ;
    }

    /*
     * Using counter, choose the next test to run and then return with true
     *  if successful.  Increments the counter.
     */
    public def nextTest(): Boolean {
        val test = [ "loop", "arithmetic", "array", "field", "method", "exception", "thread", "gc", "io" ];

        // header
        if (++next == 0) {
            StringHelper.bmfill(pstream, "Benchmark", "Time (msec)");
            StringHelper.bmfill(pstream, "----------------------------------", "----------");
            pstream.println(" ");
        }

        // run the test or the cum
        if (next < test.length) {
            runTest(test(next));
            pstream.println(" ");
            return true;
        } else if (next == test.length) {
            cumtimer.record();
            StringHelper.bmfill(pstream, "Cumulative runtime: ", cumtimer.elapsed());
            pstream.println();
            runGetProperties(pstream);
            return true;
        }

        return false; // terminates if call is in loop
    }

    /*
     * Using a string, choose the test to run and then return with true
     *  if successful.
     */
    public def runTest(str: String): Boolean {

        if (str.equals("loop")) {
            runLoopTest(pstream);
            return true;
        } else if (str.equals("arithmetic")) {
            runArithTest(pstream);
            return true;
        } else if (str.equals("array")) {
            runArrayTest(pstream);
            return true;
        } else if (str.equals("field")) {
            runFieldTest(pstream);
            return true;
        } else if (str.equals("method")) {
            runMethodTest(pstream);
            return true;
        } else if (str.equals("exception")) {
            runExceptionTest(pstream);
            return true;
        } else if (str.equals("thread")) {
            runThreadTest(pstream);
            return true;
        } else if (str.equals("gc")) {
            if (skipgc == false)
                runGCTest(pstream);
            else
                pstream.println("WARNING: GC benchmark is disabled");
            return true;
        } else if (str.equals("io")) {
            if (pstream == x10.io.Console.OUT)
                runIOTest(pstream);
            else
                pstream.println("WARNING: Cannot perform IO benchmark in applet");
            return true;
        } else if (str.equals("configuration")) {
            runGetProperties(pstream);
            return true;
        } else if (str.equals("-help") || true) {
            pstream.println("usage: java Benchmark [-help] {loop | arithmetic | array | field | method | exception | thread | gc | io | configuration}*");
            return false;
        }

        return false;
    }

    @Native("java", "System.getProperty(\"os.arch\", \"?\")")
    public static def getArch(): String { return "?"; }
    
    @Native("java", "System.getProperty(\"os.name\", \"?\")")
    public static def getOS(): String { return "?"; }
    
    @Native("java", "System.getProperty(\"os.version\", \"?\")")
    public static def getOSVersion(): String { return "?"; }
    
    @Native("java", "System.getProperty(\"java.vendor\", \"?\")")
    public static def getJavaVendor(): String { return "?"; }
    
    @Native("java", "System.getProperty(\"java.version\", \"?\")")
    public static def getJavaVersion(): String { return "?"; }
    
    public def runGetProperties(pstream: Printer): void {
        var arch: String, os: String, osversion: String, javavendor: String, javaversion: String;

        pstream.println("\nSystem Configuration");
        pstream.println("--------------------");

        arch = getArch();
        StringHelper.bmfill(pstream, "Architecture: ", arch);

        os = getOS();
        StringHelper.bmfill(pstream, "OS: ", os);

        osversion = getOSVersion();
        StringHelper.bmfill(pstream, "OS version: ", osversion);

        javavendor = getJavaVendor();
        StringHelper.bmfill(pstream, "Java interpreter vendor: ", javavendor);

        javaversion = getJavaVersion();
        
        StringHelper.bmfill(pstream, "Java version: ", javaversion);
        StringHelper.bmfill(pstream, "Benchmark version: ", version);
        StringHelper.bmfill(pstream, "Benchmark mode: ", mode);
        StringHelper.bmfill(pstream, "Benchmark optimization: ", optflags);

    }

    public static def runGCTest(pstream: Printer) {
        val gc = new GCTest();
        gc.start(pstream);
        gc.report(pstream);
    }

    public static def runLoopTest(pstream: Printer) {
        val lt = new LoopTest();
        lt.start();
        lt.report(pstream);
    }

    public static def runArrayTest(pstream: Printer) {
        val arr_t = new ArrayTest();
        arr_t.start();
        arr_t.report(pstream);
    }

    public static def runFieldTest(pstream: Printer) {
        val ft = new FieldTest();
        ft.start();
        ft.report(pstream);
    }

    public static def runArithTest(pstream: Printer) {
        val arith_t = new ArithmeticTest();
        arith_t.start();
        arith_t.report(pstream);
    }

    public static def runMethodTest(pstream: Printer) {
        val meth_t = new MethodTest();
        meth_t.start();
        meth_t.report(pstream);
    }

    public static def runExceptionTest(pstream: Printer) {
        val throw_t = new ExceptionTest();
        throw_t.start();
        throw_t.report(pstream);
    }

    public static def runThreadTest(pstream: Printer) {
        val thread_t = new ThreadTest();
        thread_t.start();
        thread_t.report(pstream);
    }

    public static def runIOTest(pstream: Printer) {
        val io_t = new IOTest();
        io_t.start(pstream);
        io_t.report(pstream);
    }
}
