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

import java.io.*;

class Timer {
    private long base_time;
    private long elapsed_time;

    private static final long UNIT = 1000000;

    public Timer() {
	clear();
    }

    public void mark() {
	base_time = System.nanoTime();
    }

    public void clear() {
	elapsed_time = 0;
    }

    public void record() {
	elapsed_time += (System.nanoTime() - base_time);
    }

    public float elapsed() {
	return ((float) elapsed_time) / UNIT;
    }

    public void report(PrintStream pstream) {
	float elapsed_seconds = elapsed();
	pstream.println("Time " + elapsed_seconds + " msec");
    }
    public void report() {
	report(System.out);
    }

}


class GCTest {
    // WGG - pick these sizes carefully: will kill some systems
    static final int GC_POINTERS = 200;
    static final int GC_OBJECTS = 2000;
    static final int MAX_OBJ_SIZE = 4000;
    GCTestElem[] objects = new GCTestElem[GC_POINTERS];
    static int objs_collected = 0;
    static long seed1 = 121393;  // WGG - no, I didn't look these up in Knuth
    static long seed2 = 196418;
    long total_size = 0;
    long freememory, totalmemory;
    long newfreememory, newtotalmemory, recoveredmemory;
    Timer fullGCtimer;
    Timer incrGCtimer;

    public GCTest() { }

    // WGG - this was Math.random(), but we need something a little less random
    //  and system-specific in order to achieve cross-platform repeatability.
    long fiborandom() {
	long sum = seed1 + seed2;
	if (sum < 0) sum *= -1;

	seed1 = seed2;
	seed2 = sum;
	return sum;
    }

    public void start(PrintStream pstream) {
	int size, which;
	Runtime RT = Runtime.getRuntime();

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

	//    try {
	for(int i = 0; i < GC_OBJECTS; i++) {
	    //      size = (int)(Math.random() * MAX_OBJ_SIZE);
	    size = (int)(fiborandom() % MAX_OBJ_SIZE);
	    total_size += size;
	    //      which = (int)(Math.random() * GC_POINTERS);
	    which = (int)(fiborandom() % GC_POINTERS) + 1; // WGG - no 0 sized objects
	    objects[which] = new GCTestElem(size);
	}
	//      } catch (Exception e) { pstream.println(e.toString()); }

	incrGCtimer.record();

    }

    public void report(PrintStream pstream) {
	pstream.println("System.gc() w/ " + freememory +
			"B memory avail of " + totalmemory + "B total");
	StringHelper.bmfill(pstream, "GC'd " + recoveredmemory +
			    "B, leaving " + newfreememory + "B of " +
			    newtotalmemory + "B total: ", 
			    fullGCtimer.elapsed());
	StringHelper.bmfill(pstream,
			    GC_OBJECTS +
			    " obj rand. new'd/assigned (avg " +
			    (total_size / GC_OBJECTS) + "B), " +
			    objs_collected + " GC'd: ",
			    incrGCtimer.elapsed());
    }
}

class GCTestElem {
    byte collect[];

    public GCTestElem(int size) {
	collect = new byte[size];
    }

    protected void finalize() {
	GCTest.objs_collected++;
    }
}

class LoopTest {
    static final int ITERATIONS = 1000000;
    Timer t;

    public void start() {
	t = new Timer();
	t.mark();
	for(int i = 0; i < ITERATIONS; i++);
	t.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream,
			    "Empty loop iterated " + ITERATIONS + " times: ",
			    t.elapsed());
    }
}

class ArrayTest {
    static final int ARRAY_INTS = 1000000;
    int[] arr = new int[ARRAY_INTS];
    Timer t;

    public void start() {
	t = new Timer();

	t.mark();
	for(int i = 0; i < ARRAY_INTS; i++) {
	    arr[i] = i;
	}
	t.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream, "Assigned to " + ARRAY_INTS + " array ints: ",
			    t.elapsed());
    }
}

class FieldTest {
    static final int FIELD_ACCESSES = 1000000;
    FieldTestElem fte = new FieldTestElem();
    Timer t;

    public void start() {
	int temp;

	t = new Timer();

	t.mark();
	for(int i = 0; i < FIELD_ACCESSES; i++) {
	    temp = fte.data;
	}
	t.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream, FIELD_ACCESSES + " object int field accesses: ",
			    t.elapsed());
    }
}

class FieldTestElem {
    int data = 1;
}

class ArithmeticTest {
    static final int ADDS = 1000000;
    static final int MULTS = 1000000;
    static final int FADDS = 1000000;
    static final int FMULTS = 1000000;
    Timer intaddtimer, intmulttimer;
    Timer doubleaddtimer, doublemulttimer;
    int sum = 0, prod = 1;
    double fsum = 0.0, fprod = 1.0;
    double realnumber = Math.random() * 43213.5752 + 1;  // WGG - don't use int i

    // WGG - note that there are extra adds hidden in the loop counter
    public void start(PrintStream pstream) {
	intaddtimer = new Timer();	
	intmulttimer = new Timer();	
	doubleaddtimer = new Timer();	
	doublemulttimer = new Timer();	

	intaddtimer.mark();
	for(int i = 0; i < ADDS; i++) {
	    //      try {
	    sum += i;
	    //        } catch(Exception e) { pstream.println("int add exception"); }
	}
	intaddtimer.record();

	intmulttimer.mark();
	for(int i = 1; i <= MULTS; i++) {
	    //      try {
	    prod *= i;
	    //        } catch(Exception e) { pstream.println("int mult exception"); }
	}
	intmulttimer.record();
  
	doubleaddtimer.mark();
	for(int i = 0; i < FADDS; i++) {
	    //      try {
	    fsum += realnumber;
	    //        } catch(Exception e) { pstream.println("double float add exception"); }
	}
	doubleaddtimer.record();

	doublemulttimer.mark();
	for(int i = 1; i <= FMULTS; i++) {
	    //     try {
	    fprod *= realnumber;
	    //        } catch(Exception e) { pstream.println("double float mult exception"); }
	}
	doublemulttimer.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream, "Added " + ADDS + " ints in loop: ",
			    intaddtimer.elapsed());
	StringHelper.bmfill(pstream, "Multipled " + MULTS + " ints in loop: ",
			    intmulttimer.elapsed());
	StringHelper.bmfill(pstream, "Added " + ADDS + " doubles in loop: ",
			    doubleaddtimer.elapsed());
	StringHelper.bmfill(pstream, "Multipled " + MULTS + " doubles in loop: ",
			    doublemulttimer.elapsed());
    }
}

class MethodTest {
    static final int METHOD_CALLS = 1000000;
    Timer sametimer, othertimer;
    MethodTest mt;

    public void start() {
	sametimer = new Timer();
	othertimer = new Timer();
	mt = new MethodTest();

	sametimer.mark();
	for(int i = 0; i < METHOD_CALLS; i++) {
	    empty();
	}
	sametimer.record();

	othertimer.mark();
	for(int i = 0; i < METHOD_CALLS; i++) {
	    mt.empty();
	}
	othertimer.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream,
			    METHOD_CALLS + " method calls in same object: ",
			    sametimer.elapsed());
	StringHelper.bmfill(pstream,
			    METHOD_CALLS + " method calls in other object: ",
			    othertimer.elapsed());
    }

    public void empty() { } 
}

class ExceptionTest {
    static final int THROWS = 1000000;
    Timer t;
    MyException exc;

    public void start() {
	t = new Timer();
	exc = new MyException();

	t.mark();
	for(int i = 0; i < THROWS; i++) {
	    try {
		throw exc;
	    } catch(MyException e) { }
	}
	t.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream,
			    "Threw and caught " + THROWS + " exceptions: ",
			    t.elapsed());
    }
}

class MyException extends Exception { }
      
class ThreadTest {
    static final int THREADCNT = 3;
    MyThread threads[] = new MyThread[THREADCNT];
    Timer t;
    int threadsDone = 0;
    int i;

    public void start() {
	t = new Timer();

	for (i = 0 ; i < THREADCNT ; i++)
	    threads[i] = new MyThread();

	t.mark();

	for (i = 0 ; i < THREADCNT ; i++)
	    threads[i].start(this);

	while(threadsDone != THREADCNT) {
	    try {
		Thread.yield();
	    } catch(Exception e) { }
	}
	t.record();
    }

    public void signal() {
	threadsDone++;
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream, THREADCNT + " threads, switched " +
			    MyThread.SWITCHES + " times each: ",
			    t.elapsed());
    }
}

class MyThread extends Thread {
    static final int SWITCHES = 10000;
    ThreadTest caller;

    public void start(ThreadTest caller) {
	this.caller = caller;
	start();
    }

    public void run() {
	for(int i = 0; i < SWITCHES; i++) {
	    try {
		Thread.yield();
	    } catch(Exception e) { }
	}

	caller.signal();
    }
}

class IOTest {
    Timer bytetimer, blocktimer;
    static final int BUFSIZE = 100000;
    byte buffer[];
    //FileOutputStream out = null;
    BufferedOutputStream out = null;

    public void start(PrintStream pstream) {
	bytetimer = new Timer();
	blocktimer = new Timer();

	try {
	    //out = new FileOutputStream("tmpfile");
	    out = new BufferedOutputStream(new FileOutputStream("tmpfile"));
	} catch(IOException e) {
	    pstream.println("Could not create \"tmpfile\" for IO benchmark");
	    return;
        }
    
	bytetimer.mark();
	for(int i = 0; i < BUFSIZE; i++) {
	    try {
		out.write(0);
	    } catch(Exception e) { }
	}
	bytetimer.record();

	buffer = new byte[BUFSIZE];
	blocktimer.mark();
	try {
	    out.write(buffer);
	} catch(Exception e) { }
	blocktimer.record();
    }

    public void report(PrintStream pstream) {
	StringHelper.bmfill(pstream, BUFSIZE + " writes of 1 byte: ",
			    bytetimer.elapsed());
	StringHelper.bmfill(pstream, "1 write of " + BUFSIZE + " bytes: ",
			    blocktimer.elapsed());
    }
}


class StringHelper {

    public static String filler(int amount, String chr) {
	String pad = "";
	for (int i = 1 ; i <= amount ; i++)
	    pad += chr;
	return pad;
    }

    public static String fill(String str1, String str2, int outto, String chr) {
	int padamount = outto - (str1.length() + str2.length());
	return str1 + filler(padamount, chr) + str2;
    }

    public static String floatfill(float num, int totalplaces) {
	String numstr = num + "";
	int numlen = numstr.length();
	int places = numlen - (numstr.indexOf('.') + 1);
	return fill(numstr, "", numlen + (totalplaces - places), "0");
    }

    public static void bmfill(PrintStream ps, String str1, String str2) {
	ps.println(fill(str1, str2, 64, " "));
    }

    public static void bmfill(PrintStream ps, String str1, float num) {
	ps.println(fill(str1, floatfill(num, 3), 64, " "));
    }

} // end class StringHelper



public class Benchmark {

    int next;
    PrintStream pstream;
    Timer cumtimer;
    boolean skipgc;
    String mode;
    String optflags;
    static final String version = "1.0";

    Benchmark(PrintStream thestream, boolean SkipGc, String OptFlags) {
	cumtimer = new Timer();
	cumtimer.mark();
	next = -1;			// preincrements to 0
	pstream = thestream;
	skipgc = SkipGc;
	mode = (pstream == System.out) ? "application" : "applet";
	optflags = (OptFlags != null) ? OptFlags : "unspecified";
    }

    public static void main(String argv[]) {
	Benchmark bm = new Benchmark(System.out, false, "unspecified");

	if (argv.length == 0)
	    bm.runbenchmarks();
	else {
	    for (int i = 0 ; i < argv.length ; i++)
		bm.runTest(argv[i]);
	}
    }

    public void runbenchmarks() {
	while (nextTest());
    }

    /*
     * Using counter, choose the next test to run and then return with true
     *  if successful.  Increments the counter.
     */
    public boolean nextTest() {
	String test[] = { "loop", "arithmetic", "array", "field", "method",
			  "exception", "thread", "gc", "io"};

	// header
	if (++next == 0) {
	    StringHelper.bmfill(pstream, "Benchmark", "Time (msec)");
	    StringHelper.bmfill(pstream, "----------------------------------", "----------");
	    pstream.println(" ");
	}

	// run the test or the cum
	if (next < test.length) {
	    runTest(test[next]);
	    pstream.println(" ");
	    return true;
	}
	else if (next == test.length) {
	    cumtimer.record();
	    StringHelper.bmfill(pstream, "Cumulative runtime: ", cumtimer.elapsed());
	    pstream.println();
	    runGetProperties(pstream);
	    return true;
	}
    
	return false;   // terminates if call is in loop
    }

    /*
     * Using a string, choose the test to run and then return with true
     *  if successful.
     */
    public boolean runTest(String str) {

	if (str.equals("loop")) {
	    runLoopTest(pstream);
	    return true;
	}
	else if (str.equals("arithmetic")) {
	    runArithTest(pstream);
	    return true;
	}
	else if (str.equals("array")) {
	    runArrayTest(pstream);
	    return true;
	}
	else if (str.equals("field")) {
	    runFieldTest(pstream);
	    return true;
	}
	else if (str.equals("method")) {
	    runMethodTest(pstream);
	    return true;
	}
	else if (str.equals("exception")) {
	    runExceptionTest(pstream);
	    return true;
	}
	else if (str.equals("thread")) {
	    runThreadTest(pstream);
	    return true;
	}
	else if (str.equals("gc")) {
	    if (skipgc == false)
		runGCTest(pstream);
	    else
		pstream.println("WARNING: GC benchmark is disabled");
	    return true;
	}
	else if (str.equals("io")) {
	    if (pstream == System.out)
		runIOTest(pstream);
	    else
		pstream.println("WARNING: Cannot perform IO benchmark in applet");
	    return true;
	}
	else if (str.equals("configuration")) {
	    runGetProperties(pstream);
	    return true;
	}
	else if (str.equals("-help") || true) {
	    pstream.println("usage: java Benchmark [-help] {loop | arithmetic | array | field | method | exception | thread | gc | io | configuration}*");
	    return false;
	}

	return false;
    }


    public void runGetProperties(PrintStream pstream) {
	String arch, os, osversion, javavendor, javaversion;

	pstream.println("\nSystem Configuration");
	pstream.println("--------------------");

	try {
	    arch = System.getProperty("os.arch", "?");
	} catch(Exception e) { arch = "<protected>";}
	StringHelper.bmfill(pstream, "Architecture: ", arch);

	try {
	    os = System.getProperty("os.name", "?");
	} catch(Exception e) { os = "<protected>";}
	StringHelper.bmfill(pstream, "OS: ", os);

	try {
	    osversion = System.getProperty("os.version", "?");
	} catch(Exception e) { osversion = "<protected>";}
	StringHelper.bmfill(pstream, "OS version: ", osversion);

	try {
	    javavendor = System.getProperty("java.vendor", "?");
	} catch(Exception e) { javavendor = "<protected>";}
	StringHelper.bmfill(pstream, "Java interpreter vendor: ", javavendor);

	try {
	    javaversion = System.getProperty("java.version", "?");
	} catch(Exception e) { javaversion = "<protected>";}
	StringHelper.bmfill(pstream, "Java version: ", javaversion);
	StringHelper.bmfill(pstream, "Benchmark version: ", version);
	StringHelper.bmfill(pstream, "Benchmark mode: ", mode);
	StringHelper.bmfill(pstream, "Benchmark optimization: ", optflags);

    }

    public static void runGCTest(PrintStream pstream) {
	GCTest gc = new GCTest();
	gc.start(pstream);
	gc.report(pstream);
    }

    public static void runLoopTest(PrintStream pstream) {
	LoopTest lt = new LoopTest();
	lt.start();
	lt.report(pstream);
    }

    public static void runArrayTest(PrintStream pstream) {
	ArrayTest arr_t = new ArrayTest();
	arr_t.start();
	arr_t.report(pstream);
    }

    public static void runFieldTest(PrintStream pstream) {
	FieldTest ft = new FieldTest();
	ft.start();
	ft.report(pstream);
    }

    public static void runArithTest(PrintStream pstream) {
	ArithmeticTest arith_t = new ArithmeticTest();
	arith_t.start(pstream);
	arith_t.report(pstream);
    }

    public static void runMethodTest(PrintStream pstream) {
	MethodTest meth_t = new MethodTest();
	meth_t.start();
	meth_t.report(pstream);
    }

    public static void runExceptionTest(PrintStream pstream) {
	ExceptionTest throw_t = new ExceptionTest();
	throw_t.start();
	throw_t.report(pstream);
    }

    public static void runThreadTest(PrintStream pstream) {
	ThreadTest thread_t = new ThreadTest();
	thread_t.start();
	thread_t.report(pstream);
    }

    public static void runIOTest(PrintStream pstream) {
	IOTest io_t = new IOTest();
	io_t.start(pstream);
	io_t.report(pstream);
    }

} // end class Benchmark
