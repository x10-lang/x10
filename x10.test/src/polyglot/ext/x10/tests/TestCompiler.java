package polyglot.ext.x10.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import x10.lang.Activity;
import x10.lang.Runtime;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;

/**
 * JUnit testing harness for the X10 compiler.
 * 
 * <h2>Overview</h2>
 * The harness helps invoke the compiler on an X10 testcase and
 * then tries to execute the resulting code in the JVM.
 * The X10 code can either consist of a 'run' method (which
 * is supposed to return 'false' on errors) or of a 
 * 'main' method (which is supposed to throw an exception on
 * errors).
 * Naturally, if the compiler gives an exception, this is also
 * an error.
 * 
 * <h2>Adding tests</h2>
 * Adding testcases is typically a one-liner: add a testXXX
 * method which calls run or runMain, passing the path to the
 * X10 source and the name of the main class as arguments.
 * For the main case, you can also pass the arguments to the
 * main method of the X10 program (if any).  So most of the
 * work is to write the x10 code that does something interesting.
 * 
 * If you are addding new tests that are currently known to fail,
 * please <em>commit</em> them with a capital "T" in the "test" of the
 * method name.  This deactivates the test, allowing others to
 * quickly check that they did not break anything when running
 * the tests in regression.  
 * 
 * <h2>Running the tests</h2>
 * Running this testcase is easy, add it as just any JUnit
 * test to eclipse.  You might want to make sure to have the
 * current working directory set to the root of the X10
 * checkout (x10/), otherwise the data/ files (xcds) and
 * the X10 source inputs will not be found by the compiler.
 * 
 * If you are not using eclipse, compile the code using
 * "ant x10-ext" and then run "bin/junit" (both from the main
 * x10 directory).  [ Note that this currently does not work,
 * it fails in the same way as x10c does, Vj said he'd look
 * into this, so I won't. ]
 * 
 * <h2>Internals</h2>
 * The harness works by using a class loader to load the
 * dumped .class files and some reflection to invoke the
 * testcase.  Some setup work is done to initialize the X10
 * Runtime.
 * 
 * @author Christian Grothoff <christian@grothoff.org>
 */
public class TestCompiler extends TestCase {

    
    public void testFutureTest() {
        runMain("examples/testcases/working/FutureTest.x10", "FutureTest");
        runMain("examples/testcases/working/FutureTest1.x10", "FutureTest1");
    }

    public void testHelloWorld() {
        runMain("examples/testcases/working/HelloWorld.x10", "HelloWorld");
    }

    public void testBoxing0() {
        runMain("examples/testcases/working/Boxing0.x10", "Boxing0");
    }

    public void testFuture0() {
        runMain("examples/testcases/working/Future0.x10", "Future0");
    }

    public void testFuture0Boxed() {
        runMain("examples/testcases/working/Future0Boxed.x10", "Future0Boxed");
    }

    public void testFuture1() {
        runMain("examples/testcases/working/Future1.x10", "Future1");
    }

    public void testFuture1Boxed() {
        runMain("examples/testcases/working/Future1Boxed.x10", "Future1Boxed");
    }

    public void testFuture2() {
        runMain("examples/testcases/working/Future2.x10", "Future2");
    }

    public void testFuture2Boxed() {
        runMain("examples/testcases/working/Future2Boxed.x10", "Future2Boxed");
    }

    public void testFuture3() {
        runMain("examples/testcases/working/Future3.x10", "Future3");
    }

    public void testFuture3Boxed() {
        runMain("examples/testcases/working/Future3Boxed.x10", "Future3Boxed");
    }

    public void testFuture4() {
        runMain("examples/testcases/working/Future4.x10", "Future4");
    }

    public void testFuture4Boxed() {
        runMain("examples/testcases/working/Future4Boxed.x10", "Future4Boxed");
    }

    public void testNullable0Ref() {
        runMain("examples/testcases/working/Nullable0Ref.x10", "Nullable0Ref");
    }

    public void testNullable1() {
        runMain("examples/testcases/working/Nullable1.x10", "Nullable1");
    }

    public void testNullable2() {
        runMain("examples/testcases/working/Nullable2.x10", "Nullable2");
    }

    public void testNullableFuture0() {
        runMain("examples/testcases/working/NullableFuture0.x10", "NullableFuture0");
    }

    public void testNullableFuture1() {
        runMain("examples/testcases/working/NullableFuture1.x10", "NullableFuture1");
    }

    public void testNullableFuture2() {
        runMain("examples/testcases/working/NullableFuture2.x10", "NullableFuture2");
    }
    
    public void TestConditionalAtomicTest() {
        run("examples/testcases/ConditionalAtomicTest.x10", "ConditionalAtomicTest");
    }
    
    public void TestAsyncTest() { // HANGS!
        run("examples/testcases/AsyncTest.x10", "AsyncTest");
    }
    
    public void TestAsyncTest1() { // HANGS!
        run("examples/testcases/AsyncTest1.x10", "AsyncTest1");
    }
    
    public void TestRangeTest() {
        run("examples/testcases/RangeTest.x10", "RangeTest");
    }
    
    public void TestClockTest() {
        run("examples/testcases/ClockTest.x10", "ClockTest");
    }
   
    public void TestClockTest2() {
        run("examples/testcases/ClockTest2.x10", "ClockTest2");
    }
   
    public void TestClockedFinalTest() {
        run("examples/testcases/ClockedFinalTest.x10", "ClockFinalTest");
    }
   
    public void TestAtomic1() { // HANGS!
        runMain("examples/testcases/working/Atomic1.x10", "Atomic1");
    }  
    
    public void TestAwait() {
        run("examples/testcases/AwaitTest.x10", "AwaitTest");
    }

    // *************** you should never have to edit anything
    // below this line **************************************
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCompiler.class);
    }

    public static TestSuite suite() {
        return new TestSuite(TestCompiler.class);
    }
    
    /**
     * Constructor for TestX10PrettyPrinterVisitor.
     * @param name
     */
    public TestCompiler(String name) {
        super(name);
    }  

    private final Activity a
        = new Activity() { public void run() {} }; // dummy
  
    /**
     * Junit may use additional threads to run the testcases
     * (other than the main one used to initialize the
     * Runtime class).  Hence we need a litte hack to register
     * the thread used to run the testcase as a 'local' thread
     * with the Runtime.
     */
    public void setUp() {
    	DefaultRuntime_c r = (DefaultRuntime_c) Runtime.runtime;
    	Place[] pls = Place.places();
    	Thread t = Thread.currentThread();
    	r.registerThread(t, pls[0]);
    	r.registerActivityStart(t, a, null);
    }


    private void compile(String file) {
        String[] poargs 
            = new String[] { "-ext", "x10", file };
        polyglot.main.Main.main(poargs); // run compiler!
    }

    private void run(String file, String main) {
        compile(file);
        try {
            ClassLoader loader 
                = new URLClassLoader(new URL[] { new URL("file://" + System.getProperty("user.dir") + "/") }); 
            Class c
                = loader.loadClass(main);
            Object inst
                = c.newInstance();
            Method m
                = c.getMethod("run", new Class[0]);
            Boolean ret = (Boolean) m.invoke(inst, null);
            assertTrue(ret.booleanValue());
        } catch (IOException io) {
            fail(io.toString());
        } catch (InstantiationException ie) {
            fail(ie.toString());
        } catch (NoSuchMethodException nmse) {
            fail(nmse.toString());
        } catch (InvocationTargetException ite) {
            fail(ite.getCause().getMessage());
        } catch (ClassNotFoundException cnfe) {
            fail(cnfe.toString());
        } catch (IllegalArgumentException iae) {
            fail(iae.toString());
        } catch (ClassFormatError cfe) {
            fail(cfe.toString());
        } catch (IllegalAccessException iae) {
            fail(iae.toString());
        }
    }
    
    private void runMain(String file, String main) {
	runMain(file, main, null);
    }

    private void runMain(String file,
                         String main,
                         String[] args) {
        compile(file);
	try {
	    ClassLoader loader 
		= new URLClassLoader(new URL[] { new URL("file://" + System.getProperty("user.dir") + "/") }); 
	    Class c
		= loader.loadClass(main);
	    Method m
	        = c.getMethod("main", new Class[] { String[].class });
	    m.invoke(null, new Object[] {args});	    
	} catch (IOException io) {
	    fail(io.toString());
	} catch (NoSuchMethodException nmse) {
	    fail(nmse.toString());
	} catch (InvocationTargetException ite) {
	    fail(ite.toString());
	} catch (ClassNotFoundException cnfe) {
	    fail(cnfe.toString());
	} catch (IllegalArgumentException iae) {
	    fail(iae.toString());
	} catch (ClassFormatError cfe) {
	    fail(cfe.toString());
	} catch (IllegalAccessException iae) {
	    fail(iae.toString());
	}
    }

}
