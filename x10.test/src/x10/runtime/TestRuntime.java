/*
 * Created on Oct 10, 2004
 */
package x10.runtime;

import junit.framework.TestCase;
import x10.compilergenerated.ClockedFinalInt;
import x10.lang.Activity;
import x10.lang.Future;
import x10.lang.Object;
import x10.lang.Runtime;

/**
 * Testcases for the X10 Runtime.  
 * This includes testcases for all classes in x10.runtime and
 * x10.lang.  We collect them all in here since all of these 
 * tests share some common setup code that prepares the Runtime
 * for use from JUnit.
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
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
        DefaultRuntime_c r = (DefaultRuntime_c) Runtime.runtime;
        Place[] pls = Place.places();
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerThread(t, pls[0]);
            tr.registerActivityStart(t, a, null);
        }
    }

    /**
     * Clean-up effects from setUp().
     */
    public void tearDown() {
        Runtime r = Runtime.runtime;
        if (r instanceof ThreadRegistry) {
            Thread t = Thread.currentThread();
            ThreadRegistry tr = (ThreadRegistry) r;
            tr.registerActivityStop(t, a);
        }
    }

    // testcases

    private static volatile int x;
    
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
            private x10.lang.Object val;
            public void run() {
                val = new x10.lang.Object();;
            }
            public Object getResult() {
                return val;
            }
        });
        assertTrue(f.force().getClass() == x10.lang.Object.class);
    }

    public void testClockNext() {
        x = 0;
        final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
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
        final Clock c = Clock.clock();
        Activity b = new Activity() {
            public void run() {
                c.doNext();
                x = 1;
                c.resume();
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
        final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
        Activity b = new Activity() {
            public void run() {
                c.doDrop();
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
        final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
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

    public void testClockNow() {
        Activity b = new Activity() {
            public void run() {
                sleep(100);
                Activity c = new Activity() {
                    public void run() {
                        sleep(100);
                        Activity d = new Activity() {
                            public void run() {
                                x = 1;
                            }
                        };                    
                        Runtime.here().runAsync(d);
                    }
                };
                Runtime.here().runAsync(c);
            }
        };
        x = 0;
        final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
        c.doNow(b);
        c.doNext();
        assertTrue(x == 1);
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
