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
 * "ant" and "ant x10rt" then run "bin/junit" (both from the main
 * x10 directory).  
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

    

    public void testArray1() {
        run("examples/testcases/feb2005/allTests/Array1.x10","Array1");
    }

    public void testArray2() {
        run("examples/testcases/feb2005/allTests/Array2.x10","Array2");
    }

    public void testArrayCopy1() {
        run("examples/testcases/feb2005/allTests/ArrayCopy1.x10","ArrayCopy1");
    }

    public void testArrayCopy2() {
        run("examples/testcases/feb2005/allTests/ArrayCopy2.x10","ArrayCopy2");
    }

    public void testArrayCopy3() {
        run("examples/testcases/feb2005/allTests/ArrayCopy3.x10","ArrayCopy3");
    }

    public void testAsyncTest() {
        run("examples/testcases/feb2005/allTests/AsyncTest.x10","AsyncTest");
    }

    public void testAsyncTest1() {
        run("examples/testcases/feb2005/allTests/AsyncTest1.x10","AsyncTest1");
    }

    public void testAteach() {
        run("examples/testcases/feb2005/allTests/Ateach.x10","Ateach");
    }

    public void testAtomic1() {
        run("examples/testcases/feb2005/allTests/Atomic1.x10","Atomic1");
    }

    public void testAtomicTest() {
        run("examples/testcases/feb2005/allTests/AtomicTest.x10","AtomicTest");
    }

    public void testAwaitTest() {
        run("examples/testcases/feb2005/allTests/AwaitTest.x10","AwaitTest");
    }

    public void testAwaitTest1() {
        run("examples/testcases/feb2005/allTests/AwaitTest1.x10","AwaitTest1");
    }

    public void testAwaitTest2() {
        run("examples/testcases/feb2005/allTests/AwaitTest2.x10","AwaitTest2");
    }

    public void testBoxing0() {
        run("examples/testcases/feb2005/allTests/Boxing0.x10","Boxing0");
    }

    public void testBoxing1() {
        run("examples/testcases/feb2005/allTests/Boxing1.x10","Boxing1");
    }

    public void testClockTest() {
        run("examples/testcases/feb2005/allTests/ClockTest.x10","ClockTest");
    }

    public void testClockTest1() {
        run("examples/testcases/feb2005/allTests/ClockTest1.x10","ClockTest1");
    }

    public void testClockTest2() {
        run("examples/testcases/feb2005/allTests/ClockTest2.x10","ClockTest2");
    }

    public void testClockedFinalTest() {
        run("examples/testcases/feb2005/allTests/ClockedFinalTest.x10","ClockedFinalTest");
    }

    public void testConditionalAtomicQueue() {
        run("examples/testcases/feb2005/allTests/ConditionalAtomicQueue.x10","ConditionalAtomicQueue");
    }

    public void testConditionalAtomicTest() {
        run("examples/testcases/feb2005/allTests/ConditionalAtomicTest.x10","ConditionalAtomicTest");
    }

    public void testDistributionTest() {
        run("examples/testcases/feb2005/allTests/DistributionTest.x10","DistributionTest");
    }

    public void testFinishTest1() {
        run("examples/testcases/feb2005/allTests/FinishTest1.x10","FinishTest1");
    }

    public void testForeach() {
        run("examples/testcases/feb2005/allTests/Foreach.x10","Foreach");
    }

    public void testFuture0() {
        run("examples/testcases/feb2005/allTests/Future0.x10","Future0");
    }

    public void testFuture1() {
        run("examples/testcases/feb2005/allTests/Future1.x10","Future1");
    }

    public void testFuture1Boxed() {
        run("examples/testcases/feb2005/allTests/Future1Boxed.x10","Future1Boxed");
    }

    public void testFuture2Boxed() {
        run("examples/testcases/feb2005/allTests/Future2Boxed.x10","Future2Boxed");
    }

    public void testFuture3() {
        run("examples/testcases/feb2005/allTests/Future3.x10","Future3");
    }

    public void testFuture3Boxed() {
        run("examples/testcases/feb2005/allTests/Future3Boxed.x10","Future3Boxed");
    }

    public void testFuture4() {
        run("examples/testcases/feb2005/allTests/Future4.x10","Future4");
    }

    public void testFuture4Boxed() {
        run("examples/testcases/feb2005/allTests/Future4Boxed.x10","Future4Boxed");
    }

    public void testFutureNullable0() {
        run("examples/testcases/feb2005/allTests/FutureNullable0.x10","FutureNullable0");
    }

    public void testFutureNullable1Boxed() {
        run("examples/testcases/feb2005/allTests/FutureNullable1Boxed.x10","FutureNullable1Boxed");
    }

    public void testFutureTest2() {
        run("examples/testcases/feb2005/allTests/FutureTest2.x10","FutureTest2");
    }

    public void testJacobi() {
        run("examples/testcases/feb2005/allTests/Jacobi.x10","Jacobi");
    }

    public void testMiscTest1() {
        run("examples/testcases/feb2005/allTests/MiscTest1.x10","MiscTest1");
    }

    public void testNopTest() {
        run("examples/testcases/feb2005/allTests/NopTest.x10","NopTest");
    }

    public void testNullable0Ref() {
        run("examples/testcases/feb2005/allTests/Nullable0Ref.x10","Nullable0Ref");
    }

    public void testNullable1() {
        run("examples/testcases/feb2005/allTests/Nullable1.x10","Nullable1");
    }

    public void testNullable2() {
        run("examples/testcases/feb2005/allTests/Nullable2.x10","Nullable2");
    }

    public void testNullable5() {
        run("examples/testcases/feb2005/allTests/Nullable5.x10","Nullable5");
    }

    public void testNullableFuture0() {
        run("examples/testcases/feb2005/allTests/NullableFuture0.x10","NullableFuture0");
    }

    public void testNullableFuture1() {
        run("examples/testcases/feb2005/allTests/NullableFuture1.x10","NullableFuture1");
    }

    public void testNullableFuture2() {
        run("examples/testcases/feb2005/allTests/NullableFuture2.x10","NullableFuture2");
    }

    public void testNullableObject() {
        run("examples/testcases/feb2005/allTests/NullableObject.x10","NullableObject");
    }

    public void testNullableObject2() {
        run("examples/testcases/feb2005/allTests/NullableObject2.x10","NullableObject2");
    }

    public void testRandomAccess() {
        run("examples/testcases/feb2005/allTests/RandomAccess.x10","RandomAccess");
    }

    public void testRegionTest() {
        run("examples/testcases/feb2005/allTests/RegionTest.x10","RegionTest");
    }

    public void testRegionTest1() {
        run("examples/testcases/feb2005/allTests/RegionTest1.x10","RegionTest1");
    }

    public void testRegionTest2() {
        run("examples/testcases/feb2005/allTests/RegionTest2.x10","RegionTest2");
    }

    public void testRegionTestIterator() {
        run("examples/testcases/feb2005/allTests/RegionTestIterator.x10","RegionTestIterator");
    }

    public void testValueClass() {
        run("examples/testcases/feb2005/allTests/ValueClass.x10","ValueClass");
    }

    public void testqueensList() {
        run("examples/testcases/feb2005/allTests/queensList.x10","queensList");
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
