import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.io.Console;
import x10.util.Random;
import x10.io.File;
import x10.io.FileWriter;

class Timer {
    private var base_time:long;
    private var elapsed_time:long;

    private global val UNIT:long = 1000;

    def this() {
        clear();
    }

    def mark(): Void {
        base_time = System.nanoTime();
    }

    def clear(): Void {
        elapsed_time = 0;
    }

    def record(): Void {
        elapsed_time += (System.nanoTime() - base_time);
    }

    def elapsed(): float {
        // return (elapsed_time / UNIT) as float;
        return (elapsed_time as float) / UNIT;
    }

    public def report(pstream: x10.io.Printer) {
        elapsed_seconds:float = elapsed();
        pstream.println("Time " + elapsed_seconds + " msec");
    }
}

// mtake not checked
class GCTest {
    
    static class Counter {
        var objs_collected:int;
        def add() {
            ++objs_collected;
        }
    }
    
    @NativeRep("java", "java.lang.Runtime", null, null)
    static class JavaRuntime {
        @Native("java", "java.lang.Runtime.getRuntime()")
        public native static def getRuntime(): JavaRuntime!;
        
        @Native("java", "#0.gc()")
        public native def gc(): Void;
        
        @Native("java", "#0.totalMemory()")
        public native def totalMemory(): long;
        
        @Native("java", "#0.freeMemory()")
        public native def freeMemory(): long;
        
        @Native("java", "#0.runFinalization()")
        public native def runFinalization() : Void;
    }

    // WGG - pick these sizes carefully: will kill some systems
    static val GC_POINTERS:int = 200;
    static val GC_OBJECTS:int = 2000;
    static val MAX_OBJ_SIZE:int = 4000;
    val objects = Rail.make[GCTestElem](GC_OBJECTS, (i:int) => null as GCTestElem);
    val counter: Counter! = new Counter();
    var seed1:long  = 121393;  // WGG - no, I didn't look these up in Knuth
    var seed2:long  = 196418;
    var total_size:long  = 0;
    var freememory:long, totalmemory:long;
    var newfreememory:long, newtotalmemory:long, recoveredmemory:long;
    var fullGCtimer:Timer!;
    var incrGCtimer:Timer!;
    
    public def this() {}
    
    // WGG - this was Math.random(), but we need something a little less random
    //  and system-specific in order to achieve cross-platform repeatability.
    def fiborandom():long = {
        var sum:long = seed1 + seed2;
        if (sum < 0) sum *= -1;

        seed1 = seed2;
        seed2 = sum;
      return sum;
    }
    
    public def start(pstream:x10.io.Printer): Void {
        var size:int, which:int;
        RT: JavaRuntime! = JavaRuntime.getRuntime();

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
        recoveredmemory = (newfreememory - freememory) -
    		      (newtotalmemory - totalmemory);

        // Test the cost of a incremental GC (at least for Sun Java)
        incrGCtimer = new Timer();
        incrGCtimer.mark();

        for (var i:int = 0; i < GC_OBJECTS; i++) {
    	    size = (fiborandom() % MAX_OBJ_SIZE) as int;
    	    total_size += size;
    	    which = ((fiborandom() % GC_POINTERS) + 1) as int; // WGG - no 0 sized objects
    	    objects(i) = new GCTestElem(counter, size);
        }

        incrGCtimer.record();
      }

      public def report(pstream: x10.io.Printer) {
          pstream.println("System.gc() w/ " + freememory + "B memory avail of " + totalmemory + "B total");
          StringHelper.bmfill(pstream, "GC'd " + recoveredmemory + "B, leaving " + newfreememory + "B of " + newtotalmemory + "B total: ", fullGCtimer.elapsed());
          StringHelper.bmfill(pstream,GC_OBJECTS + " obj rand. new'd/assigned (avg " + (total_size / GC_OBJECTS) + "B), " + counter.objs_collected + " GC'd: ", incrGCtimer.elapsed());
    }
}

class GCTestElem {
    val collect: Rail[Byte] = Rail.make[Byte](1000, (i:int) => 0 as Byte);
    val counter: GCTest.Counter!;

    public def this(counter:GCTest.Counter!, size:int) {
        this.counter = counter;
    }

    protected def finalize() {
        counter.add();
    }
}

class LoopTest {
    static val ITERATIONS:int = 1000000;
    var t: Timer!;

    public def start(): Void {
        t = new Timer();
        t.mark();
        for (var i:int = 0; i < ITERATIONS; i++)
            ;
        t.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, "Empty loop iterated " + ITERATIONS + " times: ", t.elapsed());
    }
}

class ArrayTest {
    static val ARRAY_INTS:int = 1000000;
    val arr = Rail.make[int](ARRAY_INTS, (x:int)=>0);
    var t:Timer!;

    public def start(): Void {
        // for ((i) in 0..1) arr(i) += 5;
        
        t = new Timer();
        t.mark();
        for (var i:int = 0; i < ARRAY_INTS; i++) {
            arr(i) = i;
        }
        t.record();
    }

    public def report(pstream: x10.io.Printer) {
          StringHelper.bmfill(pstream,"Assigned to " + ARRAY_INTS + " array ints: ", t.elapsed());
    }
}

class FieldTest {
    static val FIELD_ACCESSES:int = 1000000;
    val fte:FieldTestElem! = new FieldTestElem();
    var t:Timer!;

    public def start(): Void {
        var temp:int;

        t = new Timer();

        t.mark();
        for (var i:int = 0; i < FIELD_ACCESSES; i++) {
            temp = fte.data;
        }
        t.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, FIELD_ACCESSES + " object int field accesses: ", t.elapsed());
    }
}

class FieldTestElem {
    var data:int = 1;
}

class ArithmeticTest {
    static val ADDS:int = 1000000;
    static val MULTS:int = 1000000;
    static val FADDS:int = 1000000;
    static val FMULTS:int = 1000000;
    var intaddtimer:Timer!, intmulttimer:Timer!;
    var doubleaddtimer:Timer!, doublemulttimer:Timer!;
    var sum:int = 0, prod:int = 1;
    var fsum:double = 0.0, fprod:double = 1.0;
    val realnumber:double = new Random().nextDouble() * 43213.5752 + 1; // WGG - don't use int i

    // WGG - note that there are extra adds hidden in the loop counter
    public def start(): Void {
        intaddtimer = new Timer();
        intmulttimer = new Timer();
        doubleaddtimer = new Timer();
        doublemulttimer = new Timer();

        intaddtimer.mark();
        for (var i:int = 0; i < ADDS; i++) {
            sum += i;
        }
        intaddtimer.record();

        intmulttimer.mark();
        for (var i:int = 1; i <= MULTS; i++) {
            prod *= i;
        }
        intmulttimer.record();

        doubleaddtimer.mark();
        for (var i:int = 0; i < FADDS; i++) {
            fsum += realnumber;
        }
        doubleaddtimer.record();

        doublemulttimer.mark();
        for (var i:int = 1; i <= FMULTS; i++) {
            fprod *= realnumber;
        }
        doublemulttimer.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, "Added " + ADDS + " ints in loop: ", intaddtimer.elapsed());
          StringHelper.bmfill(pstream, "Multipled " + MULTS + " ints in loop: ", intmulttimer.elapsed());
          StringHelper.bmfill(pstream, "Added " + ADDS + " doubles in loop: ", doubleaddtimer.elapsed());
          StringHelper.bmfill(pstream, "Multipled " + MULTS + " doubles in loop: ", doublemulttimer.elapsed());
    }
}

class MethodTest {
    static val METHOD_CALLS:int = 1000000;
    var sametimer:Timer!, othertimer:Timer!;
    var mt:MethodTest!;

    public def start(): Void {
        sametimer = new Timer();
        othertimer = new Timer();
        mt = new MethodTest();

        sametimer.mark();
        for (var i:int = 0; i < METHOD_CALLS; i++) {
            empty();
        }
        sametimer.record();

        othertimer.mark();
        for (var i:int = 0; i < METHOD_CALLS; i++) {
            mt.empty();
        }
        othertimer.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, METHOD_CALLS + " method calls in same object: ", sametimer.elapsed());
          StringHelper.bmfill(pstream, METHOD_CALLS + " method calls in other object: ", othertimer.elapsed());
    }

    public def empty() {
    }
}

class ExceptionTest {
    static val THROWS:int = 1000000;
    var t:Timer!;
    var exc:MyException;

    public def start(): Void {
        t = new Timer();
        exc = new MyException();

        t.mark();
        for (var i:int = 0; i < THROWS; i++) {
            try {
                throw exc;
            } catch (e:MyException) {
            }
        }
        t.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, "Threw and caught " + THROWS + " exceptions: ", t.elapsed());
    }
}

class MyException extends Exception {
}

// mtake not checked
class ThreadTest {
    
    @NativeRep("java", "java.lang.Thread", null, null)
    final static class Thread {
        @Native("java", "java.lang.Thread.yield()")
        public native static def yield():Void;
    }
    
    static val THREADCNT:int = 3;
    static val SWITCHES:int = 10000;
    var t:Timer!;
    var threadsDone:int = 0;
    var i:int;

    public def start(): Void {
        t = new Timer();

        t.mark();
        
        finish {
            for (var i:int = 0; i < THREADCNT; i++) {
                async {
                    for (var j:int = 0; j < SWITCHES; j++) {
                        try {
                            Thread.yield();
                        } catch (e:Exception) {
                        }
                    }
                    signal();
                }
            }
        }

        while (threadsDone != THREADCNT) {
            try {
                Thread.yield();
            } catch (e:Exception) {
            }
        }
        t.record();
    }

    public def signal(): Void {
        threadsDone++;
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream,THREADCNT + " threads, switched " + SWITCHES + " times each: ", t.elapsed());
    }
}

class IOTest {
    var bytetimer:Timer!, blocktimer:Timer!;
    static val BUFSIZE:int = 100000;
    var buffer: Rail[Byte];
    var out: FileWriter;

    public def start(pstream:x10.io.Printer): Void {
        bytetimer = new Timer();
        blocktimer = new Timer();

        try {
            val file:File! = new File("tmpfile");
            out = file.openWrite();
        } catch (e: Exception) {
            pstream.println("Could not create \"tmpfile\" for IO benchmark");
            return;
        }

        bytetimer.mark();
        for (var i:int = 0; i < BUFSIZE; i++) {
            try {
                out.write(0);
            } catch (e: Exception) {
            }
        }
        bytetimer.record();

        buffer = Rail.make[Byte](BUFSIZE, (i:int) => 0 as Byte);
        blocktimer.mark();
        try {
            out.write(buffer);
        } catch (e: Exception) {
        }
        blocktimer.record();
    }

    public def report(pstream: x10.io.Printer): Void {
          StringHelper.bmfill(pstream, BUFSIZE + " writes of 1 byte: ", bytetimer.elapsed());
          StringHelper.bmfill(pstream, "1 write of " + BUFSIZE + " bytes: ", blocktimer.elapsed());
    }
}

class StringHelper {

    public static def filler(amount:int, chr:String): String {
        var pad:String = "";
        for (var i:int = 1; i <= amount; i++)
            pad += chr;
        return pad;
    }

    public static def fill(str1:String, str2:String, outto:int, chr:String): String {
        val padamount:int = outto - (str1.length() + str2.length());
        return str1 + filler(padamount, chr) + str2;
    }

    public static def floatfill(num:float, totalplaces:int):String {
        var numstr:String = num + "";
        var numlen:int = numstr.length();
        var places:int = numlen - (numstr.indexOf('.') + 1);
        return fill(numstr, "", numlen + (totalplaces - places), "0");
    }

    public static def bmfill(ps: x10.io.Printer, str1: String, str2:String): Void {
        ps.println(fill(str1, str2, 64, " "));
    }

    public static def bmfill(ps: x10.io.Printer, str1:String, num:float): Void {
        ps.println(fill(str1, floatfill(num, 3), 64, " "));
    }

} // end class StringHelper


class BenchmarkX10 {
    var next_:int;
    var pstream: x10.io.Printer;
    var cumtimer:Timer!;
    var skipgc:boolean;
    var mode:String;
    var optflags:String;
    static val version:String = "1.0";

    def this(thestream:x10.io.Printer, SkipGc: boolean, OptFlags: String) {
        cumtimer = new Timer();
        cumtimer.mark();
        next_ = -1; // preincrements to 0
        pstream = thestream;
        skipgc = SkipGc;
        mode = (pstream == x10.io.Console.OUT) ? "application" : "applet";
        optflags = (OptFlags != null) ? OptFlags : "unspecified";
    }

    public static def main(args:Rail[String]!): Void {
        val bm:BenchmarkX10! = new BenchmarkX10(x10.io.Console.OUT, false, "unspecified");

        if (args.length == 0)
            bm.runbenchmarks();
        else {
            for (var i:int = 0; i < args.length; i++)
                bm.runTest(args(i));
        }
    }

    public def runbenchmarks(): Void {
        while (nextTest())
            ;
    }

    /*
     * Using counter, choose the next test to run and then return with true
     *  if successful.  Increments the counter.
     */
    public def nextTest(): boolean {
        val test: Rail[String]! = [ "loop", "arithmetic", "array", "field", "method", "exception", "thread", "gc", "io" ];

        // header
        if (++next_ == 0) {
            StringHelper.bmfill(pstream, "Benchmark", "Time (msec)");
            StringHelper.bmfill(pstream, "----------------------------------",
                    "----------");
            pstream.println(" ");
        }

        // run the test or the cum
        if (next_ < test.length) {
            runTest(test(next_));
            pstream.println(" ");
            return true;
        } else if (next_ == test.length) {
            cumtimer.record();
            StringHelper.bmfill(pstream, "Cumulative runtime: ", cumtimer
                    .elapsed());
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
    public def runTest(str:String): boolean {

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
                pstream
                        .println("WARNING: Cannot perform IO benchmark in applet");
            return true;
        } else if (str.equals("configuration")) {
            runGetProperties(pstream);
            return true;
        } else if (str.equals("-help") || true) {
            pstream
                    .println("usage: java Benchmark [-help] {loop | arithmetic | array | field | method | exception | thread | gc | io | configuration}*");
            return false;
        }

        return false;
    }

    @Native("java", "System.getProperty(\"os.arch\", \"?\")")
    public native static def getArch(): String;
    
    @Native("java", "System.getProperty(\"os.name\", \"?\")")
    public native static def getOS(): String;
    
    @Native("java", "System.getProperty(\"os.version\", \"?\")")
    public native static def getOSVersion(): String;
    
    @Native("java", "System.getProperty(\"java.vendor\", \"?\")")
    public native static def getJavaVendor(): String;
    
    @Native("java", "System.getProperty(\"java.version\", \"?\")")
    public native static def getJavaVersion(): String;
    
    public def runGetProperties(pstream: x10.io.Printer): Void {
        var arch:String, os:String, osversion:String, javavendor:String, javaversion:String;

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

    public static def runGCTest(pstream: x10.io.Printer) {
        val gc:GCTest! = new GCTest();
        gc.start(pstream);
        gc.report(pstream);
    }

    public static def runLoopTest(pstream: x10.io.Printer) {
        val lt:LoopTest! = new LoopTest();
        lt.start();
        lt.report(pstream);
    }

    public static def runArrayTest(pstream: x10.io.Printer) {
        val arr_t:ArrayTest! = new ArrayTest();
        arr_t.start();
        arr_t.report(pstream);
    }

    public static def runFieldTest(pstream: x10.io.Printer) {
        val ft:FieldTest! = new FieldTest();
        ft.start();
        ft.report(pstream);
    }

    public static def runArithTest(pstream: x10.io.Printer) {
        val arith_t:ArithmeticTest! = new ArithmeticTest();
        arith_t.start();
        arith_t.report(pstream);
    }

    public static def runMethodTest(pstream: x10.io.Printer) {
        val meth_t:MethodTest! = new MethodTest();
        meth_t.start();
        meth_t.report(pstream);
    }

    public static def runExceptionTest(pstream: x10.io.Printer) {
        val throw_t:ExceptionTest! = new ExceptionTest();
        throw_t.start();
        throw_t.report(pstream);
    }

    public static def runThreadTest(pstream: x10.io.Printer) {
        val thread_t:ThreadTest! = new ThreadTest();
        thread_t.start();
        thread_t.report(pstream);
    }

    public static def runIOTest(pstream: x10.io.Printer) {
        val io_t:IOTest! = new IOTest();
        io_t.start(pstream);
        io_t.report(pstream);
    }
}
