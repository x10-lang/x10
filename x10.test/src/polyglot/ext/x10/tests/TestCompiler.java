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
 * test to eclipse. Set the current directory to the test
 * directory, e.g. examples/testcases/feb2005/allTests  
 * 
 * If you are not using eclipse, compile the code using
 * "ant" and "ant x10rt" then run "junit"  at the 
 * test directory, e.g. examples/testcases/feb2005/allTests 
 * 
 * <h2>Internals</h2>
 * The harness works by using a class loader to load the
 * dumped .class files and some reflection to invoke the
 * testcase.  Some setup work is done to initialize the X10
 * Runtime.
 * 
 * @author Christian Grothoff <christian@grothoff.org>
 * @author Kemal
 * Automatically compiled from test directory
 */
public class TestCompiler extends TestCase {

    public void test_Array1() {
        run("./Array1.x10","Array1");
    }

    public void test_Array2() {
        run("./Array2.x10","Array2");
    }

    public void test_ArrayCopy1() {
        run("./ArrayCopy1.x10","ArrayCopy1");
    }

    public void test_ArrayCopy2() {
        run("./ArrayCopy2.x10","ArrayCopy2");
    }

    public void test_ArrayCopy3() {
        run("./ArrayCopy3.x10","ArrayCopy3");
    }

    public void test_AsyncTest() {
        run("./AsyncTest.x10","AsyncTest");
    }

    public void test_AsyncTest1() {
        run("./AsyncTest1.x10","AsyncTest1");
    }

    public void test_Ateach() {
        run("./Ateach.x10","Ateach");
    }

    public void test_Atomic1() {
        run("./Atomic1.x10","Atomic1");
    }

    public void test_AtomicTest() {
        run("./AtomicTest.x10","AtomicTest");
    }

    public void test_AwaitTest() {
        run("./AwaitTest.x10","AwaitTest");
    }

    public void test_AwaitTest1() {
        run("./AwaitTest1.x10","AwaitTest1");
    }

    public void test_AwaitTest2() {
        run("./AwaitTest2.x10","AwaitTest2");
    }

    public void test_Boxing0() {
        run("./Boxing0.x10","Boxing0");
    }

    public void test_Boxing1() {
        run("./Boxing1.x10","Boxing1");
    }

    public void test_ClockTest() {
        run("./ClockTest.x10","ClockTest");
    }

    public void test_ClockTest1() {
        run("./ClockTest1.x10","ClockTest1");
    }

    public void test_ClockTest2() {
        run("./ClockTest2.x10","ClockTest2");
    }

    public void test_ClockedFinalTest() {
        run("./ClockedFinalTest.x10","ClockedFinalTest");
    }

    public void test_ConditionalAtomicQueue() {
        run("./ConditionalAtomicQueue.x10","ConditionalAtomicQueue");
    }

    public void test_ConditionalAtomicTest() {
        run("./ConditionalAtomicTest.x10","ConditionalAtomicTest");
    }

    public void test_DistributionTest() {
        run("./DistributionTest.x10","DistributionTest");
    }

    public void test_FinishTest1() {
        run("./FinishTest1.x10","FinishTest1");
    }

    public void test_Foreach() {
        run("./Foreach.x10","Foreach");
    }

    public void test_Future0() {
        run("./Future0.x10","Future0");
    }

    public void test_Future1() {
        run("./Future1.x10","Future1");
    }

    public void test_Future1Boxed() {
        run("./Future1Boxed.x10","Future1Boxed");
    }

    public void test_Future2Boxed() {
        run("./Future2Boxed.x10","Future2Boxed");
    }

    public void test_Future3() {
        run("./Future3.x10","Future3");
    }

    public void test_Future3Boxed() {
        run("./Future3Boxed.x10","Future3Boxed");
    }

    public void test_Future4() {
        run("./Future4.x10","Future4");
    }

    public void test_Future4Boxed() {
        run("./Future4Boxed.x10","Future4Boxed");
    }

    public void test_FutureNullable0() {
        run("./FutureNullable0.x10","FutureNullable0");
    }

    public void test_FutureNullable1Boxed() {
        run("./FutureNullable1Boxed.x10","FutureNullable1Boxed");
    }

    public void test_FutureTest2() {
        run("./FutureTest2.x10","FutureTest2");
    }

    public void test_ImportTest() {
        run("./ImportTest.x10","ImportTest");
    }

    public void test_ImportTestPackage1_T3() {
        run("./ImportTestPackage1/T3.x10","ImportTestPackage1.T3");
    }

    public void test_Jacobi() {
        run("./Jacobi.x10","Jacobi");
    }

    public void test_MiscTest1() {
        run("./MiscTest1.x10","MiscTest1");
    }

    public void test_NopTest() {
        run("./NopTest.x10","NopTest");
    }

    public void test_Nullable0Ref() {
        run("./Nullable0Ref.x10","Nullable0Ref");
    }

    public void test_Nullable1() {
        run("./Nullable1.x10","Nullable1");
    }

    public void test_Nullable2() {
        run("./Nullable2.x10","Nullable2");
    }

    public void test_Nullable5() {
        run("./Nullable5.x10","Nullable5");
    }

    public void test_NullableFuture0() {
        run("./NullableFuture0.x10","NullableFuture0");
    }

    public void test_NullableFuture1() {
        run("./NullableFuture1.x10","NullableFuture1");
    }

    public void test_NullableFuture2() {
        run("./NullableFuture2.x10","NullableFuture2");
    }

    public void test_NullableObject() {
        run("./NullableObject.x10","NullableObject");
    }

    public void test_NullableObject2() {
        run("./NullableObject2.x10","NullableObject2");
    }

    public void test_RandomAccess() {
        run("./RandomAccess.x10","RandomAccess");
    }

    public void test_RegionTest() {
        run("./RegionTest.x10","RegionTest");
    }

    public void test_RegionTest1() {
        run("./RegionTest1.x10","RegionTest1");
    }

    public void test_RegionTest2() {
        run("./RegionTest2.x10","RegionTest2");
    }

    public void test_RegionTestIterator() {
        run("./RegionTestIterator.x10","RegionTestIterator");
    }

    public void test_ValueClass() {
        run("./ValueClass.x10","ValueClass");
    }

    public void test_queensList() {
        run("./queensList.x10","queensList");
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

