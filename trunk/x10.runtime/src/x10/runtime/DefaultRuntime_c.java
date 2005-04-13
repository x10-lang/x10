package x10.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.WeakHashMap;

import x10.array.DoubleArray;
import x10.array.FloatArray;
import x10.array.GenericArray;
import x10.array.IntArray;
import x10.array.BooleanArray;
import x10.array.CharArray;
import x10.array.ByteArray;
import x10.array.ShortArray;
import x10.array.point_c;
import x10.array.sharedmemory.DistributionFactory;
import x10.array.sharedmemory.DoubleArray_c;
import x10.array.sharedmemory.FloatArray_c;
import x10.array.sharedmemory.GenericArray_c;
import x10.array.sharedmemory.BooleanArray_c;
import x10.array.sharedmemory.CharArray_c;
import x10.array.sharedmemory.ByteArray_c;
import x10.array.sharedmemory.ShortArray_c;
import x10.array.sharedmemory.IntArray_c;
import x10.array.sharedmemory.LongArray_c;
import x10.array.sharedmemory.RegionFactory;
import x10.compilergenerated.Parameter1;
import x10.lang.FloatReferenceArray;
import x10.lang.DoubleReferenceArray;
import x10.lang.Future;
import x10.lang.GenericReferenceArray;
import x10.lang.BooleanReferenceArray;
import x10.lang.CharReferenceArray;
import x10.lang.ByteReferenceArray;
import x10.lang.ShortReferenceArray;
import x10.lang.IntReferenceArray;
import x10.lang.LongReferenceArray;
import x10.lang.MultipleExceptions;
import x10.lang.Object;
import x10.lang.Runtime;
import x10.lang.clock;
import x10.lang.distribution;
import x10.lang.doubleArray;
import x10.lang.floatArray;
import x10.lang.booleanArray;
import x10.lang.charArray;
import x10.lang.byteArray;
import x10.lang.shortArray;
import x10.lang.intArray;
import x10.lang.longArray;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;


/**
 * Default implementation of Runtime.
 * 
 * @author Christian Grothoff, Christoph von Praun
 */
public class DefaultRuntime_c 
    extends Runtime
    implements 
        ThreadRegistry,
        ActivityInformationProvider {

    /**
     * At what place is the given thread running (needed to support
     * running multiple Places within the same VM). Note that we're
     * using a weak hash map here since the threadpool code may decide
     * to reduce the number of threads by just letting one exit, and
     * we would in that case not want to hold on to the memory.
     */
    private final Map thread2place_ = new WeakHashMap(); // <Thread,Place>

    /**
     * Which activity is the current thread executing?  This one does
     * not have to be a weak hash map since threads are explicitly 
     * removed from the map once they complete a particular activity.
     */
    private final Map thread2activity_ = new HashMap(); // <Thread,Activity>
    
    /**
     * The places of this X10 Runtime (for now a constant set).
     */
    private final Place[] places_;
    private Thread bootThread;
    
    public DefaultRuntime_c() {
    	int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
     	this.places_ = new Place[pc];
    }
    protected synchronized void initialize() {
        // do it only once
        if (places_[0] == null) {
            int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
            x10.lang.place.MAX_PLACES = pc;
    	    for (int i=0;i<pc;i++)
    	        places_[i] = new LocalPlace_c(this, this);
        }
        LocalPlace_c.initAllPlaceTimes(getPlaces());
    }

    /**
     * Run the X10 application.
     */
    protected void run(String[] args) {
        
        // first: load libraries!
        if (null != Configuration.LOAD) {
            String[] libs = Configuration.LOAD.split(":");
            for (int i=libs.length-1;i>=0;i--)
                System.loadLibrary(libs[i]);
        }
        
        java.lang.Object[] tmp = { args };
        Activity atmp = null;
        try {	
            atmp = (Activity) Class.forName(Configuration.MAIN_CLASS_NAME+"$Main")
                .getDeclaredConstructor(new Class[] { String[].class })
                .newInstance(tmp);
        } catch (Exception e) {
            System.err.println("Could not find default constructor of main class '" + Configuration.MAIN_CLASS_NAME+ "$Main" + "'!");
            throw new Error(e);
        }
        final Activity appMain = atmp;
        // ok, some magic with the boot-thread here...
        Place p0 = (Place) place.FIRST_PLACE;
        bootThread = Thread.currentThread();
        registerThread(bootThread, p0);
        final Signal signal = new Signal();
        Activity.Expr boot = new Activity.Expr() {
            public String toString() {
                return "DefaultRuntime_c boot activity";
            }
            public void run() {
                
                synchronized(signal) {
                    signal.value = true;
                    signal.notifyAll();
                }
                // now run the actual client app (wrapped in this
                // Activity.Expr since we want to use a Clock to 
                // wait for the main app to exit, but we can't use
                // a clock directly without being a proper activity).
                Clock c = (Clock) factory.getClockFactory().clock();
                c.doNow(appMain);
                Runtime.doNext();

                if (Sampling.SINGLETON != null && Configuration.DUMP_STATS_ON_EXIT) {  
                    System.out.println(Sampling.SINGLETON.toString());
                    Sampling.shutdown();
                }
            }
            public Object getResult() {
                return null;
            }
        };
        // initialize X10 runtime system
        if (Configuration.SAMPLING_FREQUENCY_MS >= 0)
            Sampling.boot(DefaultRuntime_c.this, boot);

        // run the main app
        Future f = p0.runFuture(boot, null);
        // make sure we don't accidentially initialize Sampling by
        // being too fast with 'force'.
        while (signal.value == false) {
            try {
                synchronized(signal) {
                    signal.wait();
                }
            } catch (InterruptedException ie) {            
            }
        }        
        f.force(); // use force to wait for termination!
        
        // and now the shutdown sequence!
        // places_[] should have been initialized by now ... 
        for (int i=places_.length-1;i>=0;i--)
            places_[i].shutdown();
    }
    
    public synchronized void registerThread(Thread t, Place p) {
        // this method must be synchronized because thread2place_ is not thread-safe.
        assert (t != null);
        assert (p != null);
		thread2place_.put(t, p);
	}
    
    /**
     * Notify the asl via a callback whenever the given activity starts another
     * Activity (via async, future or now).
     */
    public synchronized void registerActivitySpawnListener(Activity i,
                                                           ActivitySpawnListener asl) {
        if (i.asl_ == null) 
            i.asl_ = new ArrayList(2);
        else
            // each listener must only be registered once!
            assert (!i.asl_.contains(asl));
        i.asl_.add(asl);
    }
    
    /**
     * Notifiation that an activity terminated with an
     * exception.
     * 
     * @param a the activity that died
     * @param i the Error or RuntimeException encountered
     */
    public void registerActivityException(Activity a,
                        Throwable t) {
        Stack fini = a.finish_;
        if (fini != null)
            fini.push(t);
    }

    /**
     * X10 executes a 'finish'.  Collect all exceptions thrown
     * by all sub-activities.
     * 
     * @param a the activity of the finish (already started)
     */
    public void startFinish(final Activity a) {
        final Stack fini = new Stack();
        a.finish_ = fini;
        registerActivitySpawnListener(a,
             new FinishASL(a, fini));
    }
    class FinishASL implements ActivitySpawnListener {
        private final Activity root;
        private final Stack fini;
        FinishASL(Activity r, Stack f) { this.root = r; this.fini = f; }
        public void notifyActivitySpawn(Activity spawn,
                Activity i) {
            if ( (root.finish_ != fini) ||
                  (spawn.finish_ != null) )
                return; // YES, this line IS needed.  - CG
            
            registerActivitySpawnListener(spawn,
                    new FinishASL(i, fini));
            spawn.finish_ = fini;
        }
        public void notifyActivityTerminated(Activity a) {}
    }
    
    /**
     * An activity of a finish is done (clock has advanced).
     * Gather the collected exceptions
     * 
     * @param a the activity (still running)
     * @return null if no exceptios were thrown,
     *   otherwise the collection of exceptions
     */
    public synchronized Throwable getFinishExceptions(Activity a) {
        Stack f = a.finish_;
        // now, compute resulting exception and return it
        if (f.isEmpty())
            return null;
        if (f.size() == 1)
            return (Throwable) f.pop();
        return new MultipleExceptions(f);
    }
    

    /**
     * Notification that an activity completed.
     */
    public void registerActivityStop(Thread t, Activity a) {
        // this is called by the thread being stopped
        assert (t == Thread.currentThread());
        ArrayList v;
        synchronized(this) {
            v = a.asl_;
            a.asl_ = null;
        }
        
        // do not unnecessarily keep the lock for the following:
        if (v != null) {
            for (int i=0;i<v.size();i++) {
                // tell other threads that this thread is about to terminate.
                ActivitySpawnListener asl = (ActivitySpawnListener) v.get(i);
                asl.notifyActivityTerminated(a);
            }
        }
        
        synchronized(this) {
            thread2activity_.remove(t);
        }
    }
    
    /**
     * Notifiation that an activity was started.
     * 
     * @param t the thread that runs the activity
     * @param a the activity that is being run
     * @param i the activity that started a (null for boot/main).
     */
    public void registerActivityStart(Thread t,
            Activity a,
            Activity i) {
        // this is called by the started thread!
        assert (t == Thread.currentThread());
        assert a != i;
        ArrayList v;    
        synchronized(this) {
            assert thread2place_.get(t) != null;
            a.place_ = (Place) thread2place_.get(t);
            thread2activity_.put(t,a);
            if (i == null) 
                return;        
            v = i.asl_;
        }
        if (v != null) {
            for (int j=0;j<v.size();j++) {
                // tell other activities that want to know that a has been spawned
                ActivitySpawnListener asl = (ActivitySpawnListener) v.get(j);
                asl.notifyActivitySpawn(a, i);
            }
        }
    }

    public synchronized Place currentPlace() {
        if (places_[0] == null) 
            initialize();
        if (places_.length == 1)
            return places_[0]; // fast path for simple test environments!
    	Thread t = Thread.currentThread();
        Place ret;
        if (t instanceof LocalPlace_c.PoolRunner) {
            LocalPlace_c.PoolRunner pr = (LocalPlace_c.PoolRunner) t;
            ret = pr.place;
        } else {
            ret = (Place) thread2place_.get(t);
        }
    	if (ret == null)
    		throw new Error("This thread is not an X10 thread!");
    	return ret;
    }

    /**
     * this method should not be exposed to x10.lang and 
     * application programmers, because the X10 programming model 
     * does not know such construct (Places are obtained indirectly 
     * through distributions).
     *  
     * @return All places available in this VM.
     */
    public Place[] getPlaces() {
        if (places_[0] == null) 
            initialize();
        // return defensive copy
        Place[] p = new Place[places_.length];
        System.arraycopy(places_, 0, p, 0, places_.length);
    	
        // the following does not work on the IBM JDK 1.4.2
        // return (Place[]) places_.clone();
        
        return places_;
    }
    
    public Factory getFactory() {
    	Factory f = new Factory() {
    		public region.factory getRegionFactory() {
    			return new RegionFactory();
    		}
    		public distribution.factory getDistributionFactory() {
    			return new DistributionFactory();
    		}
    		public point.factory getPointFactory() {    	
    			return new point_c.factory();
    		}
    		public clock.factory getClockFactory() {
    			return new clock.factory() {
    				public clock clock() {
    					return new Clock(DefaultRuntime_c.this);
    				}
    			};
    		}
    		public booleanArray.factory getBooleanArrayFactory() {
                return new BooleanArray.factory() {
                    public BooleanReferenceArray BooleanReferenceArray(distribution d, boolean c) {
                        return new BooleanArray_c( d, c, true);
                    }
                    public BooleanReferenceArray BooleanReferenceArray(distribution d, booleanArray.pointwiseOp f) {
                        return new BooleanArray_c( d, f, true);
                    }
                    public booleanArray booleanValueArray(distribution d, boolean c) {
                        return new BooleanArray_c(d, c, true, false);
                    }
                    public booleanArray booleanValueArray(distribution d, booleanArray.pointwiseOp f) {
                        return new BooleanArray_c(d, f, true, false);
                    }
                };
            }
            public charArray.factory getCharArrayFactory() {
                return new CharArray.factory() {
                    public CharReferenceArray CharReferenceArray(distribution d, char c) {
                        return new CharArray_c( d, c, true);
                    }
                    public CharReferenceArray CharReferenceArray(distribution d, charArray.pointwiseOp f) {
                        return new CharArray_c( d, f, true);
                    }
                    public charArray charValueArray(distribution d, char c) {
                        return new CharArray_c(d, c, true, false);
                    }
                    public charArray charValueArray(distribution d, charArray.pointwiseOp f) {
                        return new CharArray_c(d, f, true, false);
                    }
                };
            }
            public byteArray.factory getByteArrayFactory() {
                return new ByteArray.factory() {
                    public ByteReferenceArray ByteReferenceArray(distribution d, byte c) {
                        return new ByteArray_c( d, c, true);
                    }
                    public ByteReferenceArray ByteReferenceArray(distribution d, byteArray.pointwiseOp f) {
                        return new ByteArray_c( d, f, true);
                    }
                    public byteArray byteValueArray(distribution d, byte c) {
                        return new ByteArray_c(d, c, true, false);
                    }
                    public byteArray byteValueArray(distribution d, byteArray.pointwiseOp f) {
                        return new ByteArray_c(d, f, true, false);
                    }
                };
            }
            public shortArray.factory getShortArrayFactory() {
                return new ShortArray.factory() {
                    public ShortReferenceArray ShortReferenceArray(distribution d, short c) {
                        return new ShortArray_c( d, c, true);
                    }
                    public ShortReferenceArray ShortReferenceArray(distribution d, shortArray.pointwiseOp f) {
                        return new ShortArray_c( d, f, true);
                    }
                    public shortArray shortValueArray(distribution d, short c) {
                        return new ShortArray_c(d, c, true, false);
                    }
                    public shortArray shortValueArray(distribution d, shortArray.pointwiseOp f) {
                        return new ShortArray_c(d, f, true, false);
                    }
                };
            }
            public intArray.factory getIntArrayFactory() {
    			return new IntArray.factory() {
    				public IntReferenceArray IntReferenceArray(distribution d, int c) {
    					return new IntArray_c( d, c, true);
    				}
    				public IntReferenceArray IntReferenceArray(distribution d, intArray.pointwiseOp f) {
    					return new IntArray_c( d, f, true);
    				}
    				public intArray intValueArray(distribution d, int c) {
    					return new IntArray_c(d, c, true, false);
    				}
    				public intArray intValueArray(distribution d, intArray.pointwiseOp f) {
    					return new IntArray_c(d, f, true, false);
    				}
    			};
    		}
    		public longArray.factory getLongArrayFactory() {
    			return new longArray.factory() {
    				public LongReferenceArray LongReferenceArray(distribution d, long c) {
    					return new LongArray_c( d, c, true);
    				}
    				public LongReferenceArray LongReferenceArray(distribution d, longArray.pointwiseOp f) {
    					return new LongArray_c( d, f, true);
    				}
    				public longArray longValueArray(distribution d, long c) {
    					return new LongArray_c(d, c, true, false);
    				}
    				public longArray longValueArray(distribution d, longArray.pointwiseOp f) {
    					return new LongArray_c(d, f, true, false);
    				}
    			};
    		}
    		
            public FloatArray.factory getFloatArrayFactory() {
                return new floatArray.factory() {
                    public FloatReferenceArray FloatReferenceArray(distribution d, float c) {
                        return new FloatArray_c( d, c, true);
                    }
                    public FloatReferenceArray FloatReferenceArray(distribution d, floatArray.pointwiseOp f) {
                        return new FloatArray_c( d, f, true);
                    }
                    public floatArray floatValueArray(distribution d, float c) {
                        return new FloatArray_c(d, c, true, false);
                    }
                    public floatArray floatValueArray(distribution d, floatArray.pointwiseOp f) {
                        return new FloatArray_c(d, f, true, false);
                    }
                };              
            }
            
            public DoubleArray.factory getDoubleArrayFactory() {
    			return new doubleArray.factory() {
    				public DoubleReferenceArray DoubleReferenceArray(distribution d, double c) {
    					return new DoubleArray_c( d, c, true);
    				}
    				public DoubleReferenceArray DoubleReferenceArray(distribution d, doubleArray.pointwiseOp f) {
    					return new DoubleArray_c( d, f, true);
    				}
    				public doubleArray doubleValueArray(distribution d, double c) {
    					return new DoubleArray_c(d, c, true, false);
    				}
    				public doubleArray doubleValueArray(distribution d, doubleArray.pointwiseOp f) {
    					return new DoubleArray_c(d, f, true, false);
    				}
    			};   			
    		}
            
            public GenericArray.factory getGenericArrayFactory() {
            return new x10.lang.genericArray.factory() {
                public GenericReferenceArray GenericReferenceArray(distribution d, Parameter1 c) {
                    return new GenericArray_c( d, c, true);
                }
                public GenericReferenceArray GenericReferenceArray(distribution d, x10.lang.genericArray.pointwiseOp f) {
                    return new GenericArray_c( d, f, true);
                }
                public x10.lang.genericArray GenericValueArray(distribution d, Parameter1 c) {
                    return new GenericArray_c(d, c, true, false);
                }
                public x10.lang.genericArray GenericValueArray(distribution d, x10.lang.genericArray.pointwiseOp f) {
                    return new GenericArray_c(d, f, true, false);
                }
            };
            
        }

            
            public place.factory getPlaceFactory() {
    			return new place.factory() {
    				public place place(int i ) {
    					int index =( i %  place.MAX_PLACES);
    					if (places_[index] == null) initialize();
    					return places_[index];
    				}
    				/** Return the set of places from place(0) to place(last) (inclusive).
    				 * 
    				 */
    				public Set/*<place>*/ places (int last) {
    					if (places_[0] == null) initialize();
    					Set result = new TreeSet();
    					for (int i=0; i <= last % (place.MAX_PLACES); i++)
    						result.add(places_[i]);
    					return result;
    				}
    				public place here() {
    					return currentPlace();
    				}
    			};
    		}
    	};
    	return f;
    	
    }
    			      
    /**
     * Get the Activity object that is executing this 
     * method.
     * @return
     */
    public synchronized Activity getCurrentActivity() {
        Activity a = (Activity) thread2activity_.get(Thread.currentThread());
        if (a == null) {
            if (Thread.currentThread() == bootThread)
                return magic_boot; // magic 'boot' thread!
            throw new Error("This thread is not an X10 Thread running X10 code!");
        }
        return a;
    }
    private static Activity magic_boot = new Activity() {
        public String toString() {
            return "MagicBoot Activity";
        }
        public void run() {};
    };

    /**
     * At which place is the given activity running?  Note that this particular
     * implementation is not very efficient (since we do not have an explicit
     * mapping in this direction).  The reason is that this method is currently
     * only used for profiling and we thus do not want to have any overhead on 
     * the common path.
     * 
     * @param a 
     * @return null if the activity is not running anywhere
     */
    public synchronized Place getPlaceOfActivity(Activity a) {
        // this method must be synchronized to protect the map activity2place_
        return a.place_;
    }    

    static class Signal { boolean value; }
    
} // end of DefaultRuntime_c
