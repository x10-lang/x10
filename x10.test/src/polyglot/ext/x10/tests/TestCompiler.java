package polyglot.ext.x10.tests;

import junit.framework.TestCase;
import java.io.*;
import java.lang.reflect.*;

/**
 * @author Christian Grothoff
 */
public class TestCompiler extends TestCase {
  
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCompiler.class);
    }

    /**
     * Constructor for TestX10PrettyPrinterVisitor.
     * @param name
     */
    public TestCompiler(String name) {
        super(name);
    }  

    public void testFuture0() {
	run("examples/testcases/frontendworking/Future0.x10");
    }

    private void run(String file) {
	run(file, null);
    }

    private void run(String file, String[] args) {
	String[] poargs 
	    = new String[] { "-ext", "x10",
			     file };
	polyglot.main.Main.main(poargs); // run compiler!
	try {
	    FileInputStream fis 
		= new FileInputStream(file.replaceAll(".x10",".class"));
	    
	    final byte[] code 
		= new byte[fis.available()];
	    fis.read(code);
	    ClassLoader loader 
		= new ClassLoader() {
			public Class loadClass(String name) 
			    throws ClassNotFoundException {
			    if (name == "Code")
				return defineClass(null, code, 0, code.length);
			    else
				return super.loadClass(name);
			}
		    };
	    Class c
		= loader.loadClass("Code");
	    Method m
		= c.getMethod("main", new Class[] { String[].class });
	    m.invoke(null, args);	    
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
