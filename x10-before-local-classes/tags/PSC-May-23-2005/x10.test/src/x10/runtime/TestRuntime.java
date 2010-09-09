/*
 * Created on Oct 10, 2004
 */
package x10.runtime;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import x10.compilergenerated.ClockedFinalInt;
import x10.lang.Future;
import x10.lang.Object;
import x10.lang.Runtime;
import x10.runtime.Clock;

/**
 * Testcases for the X10 Runtime.  
 * This includes testcases for all classes in x10.runtime and
 * x10.lang.  We collect them all in here since all of these 
 * tests share some common setup code that prepares the Runtime
 * for use from JUnit.
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public class TestRuntime extends TestCase {

    static {
        Configuration.parseCommandLine(null);
        Runtime.init();
    }
    
    public static TestSuite suite() {
        return new TestSuite(TestRuntime.class);
    }
    
   /*public void runTest() {
    	TestCase test= new TestRuntime("main"); 
    	test.run();
    }*/
    
   public static void main(String[] args) {
        junit.textui.TestRunner.run(TestRuntime.class);
        Place[] pls = Place.places();
        for (int i=pls.length-1;i>=0;i--)
            pls[i].shutdown();
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
        assert (r instanceof ThreadRegistry);
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
        DefaultRuntime_c r = (DefaultRuntime_c) Runtime.runtime;
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
        }, null);
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
        }, null);
        assertTrue(f.force().getClass() == x10.lang.Object.class);
    }

    public void testClockNext() {
        x = 0;
        Runtime.here().runAsync(new Activity() {
        	public void run() {
        		Runtime.here().runAsync(new Activity() {
        			public void run() {
        				final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
        				Activity b = new Activity() {
        					public void run() {
        						Runtime.doNext();
        						x = 1;
        						Runtime.doNext();
        						Runtime.doNext();
        						x = 2;
        						Runtime.doNext();
        					}
        				};
        				Runtime.here().runAsync(b, Runtime.getCurrentActivityInformation());
        				sleep(100); // wait for activity to hit first 'doNext'
        				assertTrue(x == 0);
        				Runtime.doNext();
        				Runtime.doNext();
        				assertTrue(x == 1);
        				Runtime.doNext();
        				Runtime.doNext();
        				assertTrue( x == 2);
        			}
        		}, 
				Runtime.getCurrentActivityInformation());
        	}
        }, null);
    }
    
    
    public void testClockContinue() {
    	x = 0;
    	Runtime.here().runAsync(new Activity() {
    		public void run() {
    			Runtime.here().runAsync(new Activity() {
    				public void run() {
    					final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
    					Activity b = new Activity() {
    						public void run() {
    							Runtime.doNext();
    							x = 1;
    							c.resume();
    							sleep(100); // wait for activity to hit first 'doNext'
    							x = 2; // 'bad' coding style :-)
    						}
    					};
    					Runtime.here().runAsync(b, null);
    					assertTrue(x == 0);
    					Runtime.doNext();
    					Runtime.doNext();
    					assertTrue(x == 1);
    					sleep(200); // sleep longer than 'b'
    					assertTrue(x == 2);
    				}
    			}, Runtime.getCurrentActivityInformation());
    		}
    	}, null);
    }
    
    public void testClockDrop() {
    	Runtime.here().runAsync(new Activity() {
    		public void run() {
    			Runtime.here().runAsync(new Activity() {
    				public void run() {
    					final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
    					Activity b = new Activity() {
    						public void run() {
    							c.drop();
    						}
    					};
    					Runtime.here().runAsync(b, null);
    					Runtime.doNext();
    					Runtime.doNext();
    					Runtime.doNext();
    					Runtime.doNext();
    					Runtime.doNext();
    				}
    			}, Runtime.getCurrentActivityInformation());
    		}
    	}, null);
    }
    
    public void testClockedFinal() {
    	Runtime.here().runAsync(new Activity() {
    		public void run() {
    			Runtime.here().runAsync(new Activity() {
    				public void run() {
    					final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
    					final ClockedFinalInt i = new ClockedFinalInt(c, 0);
    					Activity b = new Activity() {
    						public void run() {
    							i.next = 1;
    							Runtime.doNext();
    							i.next = 2;
    							Runtime.doNext();
    							i.next = 3;
    							Runtime.doNext();
    						}
    					};       
    					Runtime.here().runAsync(b, null);
    					assertTrue(i.current == 0);
    					Runtime.doNext();
    					assertTrue(i.current == 1);
    					Runtime.doNext();
    					assertTrue(i.current == 2);
    					Runtime.doNext();
    					assertTrue(i.current == 3);
    					Runtime.doNext();
    				}
    			}, Runtime.getCurrentActivityInformation());
    		}
    	}, null);
    }

    public void testClockNow() {
    	Runtime.here().runAsync(new Activity() {
    		public void run() {
    			Runtime.here().runAsync(new Activity() {
    				public void run() {
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
    									Runtime.here().runAsync(d, Runtime.getCurrentActivityInformation());
    								}
    							};
    							Runtime.here().runAsync(c, null);
    						}
    					};
    					x = 0;
    					final Clock c = (Clock) Runtime.factory.getClockFactory().clock();
    					c.doNow(b);
    					Runtime.doNext();
    					assertTrue(x == 1);
    				}
    			}, Runtime.getCurrentActivityInformation());
    		}
    	}, null);
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
