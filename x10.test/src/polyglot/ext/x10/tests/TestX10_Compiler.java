/*
 * Created by vj on Jan 11, 2005
 */
package polyglot.ext.x10.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import x10.lang.Runtime;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.util.ConfigurationError;

/**
 * JUnit testing harness for the X10 compiler.
 * 
 * <h2>Overview</h2>
 * The harness helps invoke the compiler on an X10 testcase and
 * then tries to execute the resulting code in the JVM.
 * The X10 code can either consist of a 'run' method (which
 * is supposed to return 'false' on errors, or throw an exception).
 * Naturally, if the compiler gives an exception, this is also
 * an error.
 *
 * <h2>Adding test cases</h2>
 * The file TestCompiler.java contains the test cases.
 * It is generated automatically by the makeTestCompiler script,
 * from the current contents of the test directory, e.g.
 * examples/

 * <h2>Running the tests</h2>
 * Running this testcase is easy, add it as just any JUnit
 * test to eclipse. Set the current directory to the test
 * directory, e.g. examples/
 * 
 * If you are not using eclipse, compile the code using
 * "ant clobber all" then run "junit"  at the 
 * test directory, e.g. examples/
 * 
 * <h2>Internals</h2>
 * The harness works by using a class loader to load the
 * dumped .class files and some reflection to invoke the
 * testcase.  Some setup work is done to initialize the X10
 * Runtime.
 * 
 * @author Christian Grothoff <christian@grothoff.org>
 * @author vj -- Refactored to pull out the core code.
 * @author kemal -- supporting new test hierarchy
 */
/**
 * @author vj Jan 11, 2005
 * @author rfuhrer,igor Aug 3, 2006
 */
public class TestX10_Compiler extends TestCase {
    static {
	try {
	    Configuration.parseCommandLine(null);
	} catch (ConfigurationError e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	}
	Runtime.init();
    }

    private boolean fTimedout;

    public TestX10_Compiler(String name) {
	super(name);
    }

    public static void Main(Class k) {
	junit.textui.TestRunner.run(k);
    }

    public static TestSuite suite(Class k) {
	return new TestSuite(k);
    }

    protected void compile(String dir, String file) {
	dir= "../x10.common/examples/" + dir;
	String srcPath= dir + ";" + "../x10.common/examples/x10lib";
	String[] poargs= new String[] { "-sourcepath", srcPath, "-d", dir, "-ext", "x10", dir + file.substring(1) };
	polyglot.main.Main.main(poargs); // run compiler!
    }

    protected void runHelper() {
	String name= getName().substring(5);
	int pathEnd= name.lastIndexOf('_');
	if (name.substring(pathEnd+1).startsWith("MustFail"))
	    pathEnd= name.lastIndexOf('_', pathEnd-1);
	String path= "./" + name.substring(0, pathEnd).replace('_', '/');
	String shortName= name.substring(pathEnd+1).replace('$', '_');
	String x10SrcName= "./" + shortName + ".x10";

	run(x10SrcName, shortName, path);
    }

    protected void run(final String file, final String main, final String dir) {
	final Activity testActivity= new Activity() {
	    public void runX10Task() {
		try {
		    compile(dir, file);
		    String cwd= System.getProperty("user.dir") + "/../x10.common/examples/" + dir;
		    ClassLoader loader= new URLClassLoader(new URL[] { new URL("file://" + cwd + "/") });
		    Class c= loader.loadClass(main);
		    Object inst= c.newInstance();
		    Method m= c.getMethod("executeAsync", new Class[0]);
		    Thread timer= startTimeoutTimer(10);
		    Boolean ret= (Boolean) m.invoke(inst, null);
		    assertFalse("Timeout exceeded.", fTimedout);
		    assertTrue(ret.booleanValue());
		    System.out.println("Test " + main + " completed.");
		    timer.interrupt();
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
		    iae.printStackTrace();
		    fail(iae.toString());
		}
	    }
	    /**
	     * Start the timeout timer for the number of seconds specified in the
	     * "x10test.timeout" system property (default is 300).
	     * @return the timer thread.
	     */
	    private Thread startTimeoutTimer(final int seconds) {
		Thread timer = new Thread(new Runnable() {
		    public void run() {
			if (Runtime.sleep(seconds*1000)) {
			    fTimedout= true;
			    Runtime.runtime.shutdown();
			}
		    }
		});
		timer.start();
		return timer;
	    }
	};
	Runtime.runtime.prepareForBoot();
	Runtime.runtime.run(testActivity);
	if (!fTimedout)
	    Runtime.runtime.shutdown();
    }
}
