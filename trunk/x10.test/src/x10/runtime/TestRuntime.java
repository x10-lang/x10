/*
 * Created on Oct 10, 2004
 */
package x10.runtime;

import junit.framework.TestCase;
import x10.lang.Activity;
import x10.lang.Future;
import x10.lang.Runtime;
import x10.lang.X10Object;

/**
 * @author Christian Grothoff
 */
public class TestRuntime extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestRuntime.class);
    }

    public TestRuntime(String name) {
        super(name);
    }

    private final Activity a
        = new Activity() { public void run() {} }; // dummy
    
    static volatile int x;
    
    /**
     * Junit may use additional threads to run the testcases
     * (other than the main one used to initialize the
     * Runtime class).  Hence we need a litte hack to register
     * the thread used to run the testcase as a 'local' thread
     * with the Runtime.
     */
    public void setUp() {
        Runtime r = Runtime._;
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerThread(t,
                              r.initializePlaces()[0]);
            tr.registerActivityStart(t, a, null);
        }
    }

    public void tearDown() {
        Runtime r = Runtime._;
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerActivityStop(t, a);
        }
    }

    
    public void testPlaceRunAsync() {
        x = 0;
        Runtime.here().runAsync(new Activity() {
            public void run() {
                x = 1;
            }
        });
        synchronized(this) {
            try {
                this.wait(100);
            } catch (InterruptedException ie) {}
        }
        assertTrue(x == 1);
    }

    public void testPlaceRunFuture() {
        x = 0;
        Future f = Runtime.here().runFuture(new Activity.Expr() {
            private Object val;
            public void run() {
                val = this;
            }
            public X10Object getResult() {
                return new X10Object();
            }
        });
        assertTrue(f.force().getClass() == X10Object.class);
    }

    
    
} // end of TestRuntime
