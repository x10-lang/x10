/*
 * Created on Oct 10, 2004
 */
package x10.runtime;

import junit.framework.TestCase;
import x10.compilergenerated.ClockedFinalInt;
import x10.lang.Activity;
import x10.lang.Clock;
import x10.lang.Future;
import x10.lang.Runtime;
import x10.lang.X10Object;

/**
 * Testcases for the X10 Runtime.  
 * This includes testcases for all classes in x10.runtime and
 * x10.lang.  We collect them all in here since all of these 
 * tests share some common setup code that prepares the Runtime
 * for use from JUnit.
 * 
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

    /**
     * Clean-up effects from setUp().
     */
    public void tearDown() {
        Runtime r = Runtime._;
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerActivityStop(t, a);
        }
    }

    // testcases

    private static volatile int x;
    private static volatile int y;
    
    public void testPlaceRunAsync() {
        x = 0;
        Runtime.here().runAsync(new Activity() {
            public void run() {
                x = 1;
            }
        });
        sleep(100);
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

    public void testClockNext() {
        x = 0;
        final Clock c = Runtime._.createClock();
        Activity b = new Activity() {
            public void run() {
                c.doNext();
                x = 1;
                c.doNext();
                c.doNext();
                x = 2;
                c.doNext();
            }
        };
        c.register(b);
        Runtime.here().runAsync(b);
        sleep(100); // wait for activity to hit first 'doNext'
        assertTrue(x == 0);
        c.doNext();
        c.doNext();
        assertTrue(x == 1);
        c.doNext();
        c.doNext();
        assertTrue(x == 2);
    }
    
    
    public void testClockContinue() {
        x = 0;
        final Clock c = Runtime._.createClock();
        Activity b = new Activity() {
            public void run() {
                c.doNext();
                x = 1;
                c.doContinue();
                sleep(100); // wait for activity to hit first 'doNext'
                x = 2; // 'bad' coding style :-)
            }
        };
        c.register(b);
        Runtime.here().runAsync(b);
        assertTrue(x == 0);
        c.doNext();
        c.doNext();
        assertTrue(x == 1);
        sleep(200); // sleep longer than 'b'
        assertTrue(x == 2);
    }
    
    public void testClockDrop() {
        final Clock c = Runtime._.createClock();
        Activity b = new Activity() {
            public void run() {
                c.drop();
            }
        };
        c.register(b);
        Runtime.here().runAsync(b);
        c.doNext();
        c.doNext();
        c.doNext();
        c.doNext();
        c.doNext();
    }
    
    public void testClockedFinal() {
        final Clock c = Runtime._.createClock();
        final ClockedFinalInt i = new ClockedFinalInt(c, 0);
        Activity b = new Activity() {
            public void run() {
                i.next = 1;
                c.doNext();
                i.next = 2;
                c.doNext();
                i.next = 3;
                c.doNext();
            }
        };
        c.register(b);
        Runtime.here().runAsync(b);
        assertTrue(i.current == 0);
        c.doNext();
        assertTrue(i.current == 1);
        c.doNext();
        assertTrue(i.current == 2);
        c.doNext();
        assertTrue(i.current == 3);
        c.doNext();
    }

    /**
     * Helper method to delay execution (to ensure other threads
     * run a bit).
     * @param delay how long to wait
     */
    private synchronized void sleep(long delay) {
        try {
            this.wait(delay);
        } catch (InterruptedException ie) {}
    }


} // end of TestRuntime
