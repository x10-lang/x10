package x10.runtime;

import java.util.Set;

import java.util.TreeSet;


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

import x10.lang.GenericReferenceArray;
import x10.lang.BooleanReferenceArray;
import x10.lang.CharReferenceArray;
import x10.lang.ByteReferenceArray;
import x10.lang.ShortReferenceArray;
import x10.lang.IntReferenceArray;
import x10.lang.LongReferenceArray;

import x10.lang.Runtime;
import x10.lang.clock;
import x10.lang.dist;
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
 * @author vj
 */
public class DefaultRuntime_c extends Runtime {
   
    
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
    	        places_[i] = new LocalPlace_c();
    	    place.initialize();
        }
        LocalPlace_c.initAllPlaceTimes(getPlaces());
    }

    /**
     * Run the X10 application.
     */
    protected void run(String[] args) {   
    	if (Report.should_report("activity", 5)) {
    		Thread t = Thread.currentThread();
    		int tCount = Thread.activeCount();
    		Report.report(5, t + "@"+System.currentTimeMillis()+" starts in group " + t.getThreadGroup() 
    				+ " with " + tCount + " threads active.");
    		Thread[] a = new Thread[tCount];
    		int count = Thread.enumerate(a);
    		
    		for (int i = 0; i < count; i++) {
    			
    			Report.report(5, "Thread " + (a[i] == null ? "null" : a[i].getName()) + " is active.");
    		}
    	}
    	// first: load libraries!
    	if (null != Configuration.LOAD) {
    		String[] libs = Configuration.LOAD.split(":");
    		for (int i=libs.length-1;i>=0;i--)
    			System.loadLibrary(libs[i]);
    	}
    	
    	java.lang.Object[] tmp = { args };
    	Activity atmp = null;
    	try {	
    		if (Report.should_report("activity", 5)) {
    			Report.report(5, Thread.currentThread() + " " + this + " starting user class |" 
    					+ Configuration.MAIN_CLASS_NAME+ "|.");
    		}
    		atmp = (Activity) Class.forName(Configuration.MAIN_CLASS_NAME+"$Main")
			.getDeclaredConstructor(new Class[] { String[].class })
			.newInstance(tmp);
    	} catch (Exception e) {
    		System.err.println("Could not find default constructor of main class '" 
    				+ Configuration.MAIN_CLASS_NAME+ "$Main" + "'!");
    		throw new Error(e);
    	}
    	final Activity appMain = atmp;
    	final LocalPlace_c.StartSignal startSignal = new LocalPlace_c.StartSignal();
    	Activity boot = new Activity() {
            public String myName() {
                return "Boot activity";
            }
            public void run() {
            	Thread t = Thread.currentThread();
             	if (Report.should_report("activity", 5)) {
            		Report.report(5, t + "@" + System.currentTimeMillis() 
            				+ " starts running the Boot Activity.");
            	}
            
            	finishRun(appMain);
            	
            	if (Report.should_report("activity", 5)) {
            		Report.report(5, t+ "@"+System.currentTimeMillis() 
            				+ " finishes running the Boot Activity.");
            				
            	}
            	
            	if (Sampling.SINGLETON != null && Configuration.DUMP_STATS_ON_EXIT) {  
            		System.out.println(Sampling.SINGLETON.toString());
            		Sampling.shutdown();
            	}
            	// and now the shutdown sequence!
            	// places_[] should have been initialized by now ... 
            	for (int i=places_.length-1; i >= 0;i--) {
            		if (Report.should_report("activity", 5)) {
                		Report.report(5, t+ "@"+System.currentTimeMillis() 
                				+ " shutting down " + places_[i]);
                				
                	}
            		places_[i].shutdown();
            	}
            	
            	if (Report.should_report("activity", 5)) {
            		Report.report(5, t+ "@"+System.currentTimeMillis() 
            				+ " terminates.");
            				
            	}
            	// The VM goes bye bye.
            	Runtime.x10Exit();
            }
            
        };
        // initialize X10 runtime system
        if (false && Configuration.SAMPLING_FREQUENCY_MS >= 0)
            Sampling.boot(DefaultRuntime_c.this, boot);

        // run the main app.
        Runtime.runAsync(boot);
        
        /*synchronized (startSignal) {
            try {
                while (! startSignal.go) {
                    startSignal.wait();
                }
            } catch (InterruptedException ie) {
                System.err.println("LocalPlace_c::runAsync - unexpected exception " + ie);
                throw new Error(ie); // should never happen!
            }
        } */
        
        // Main thread terminates. bootActivity will now carry on.
        // VM terminates when bootActivity terminates.
        int tCount = Thread.activeCount();
     	if (Report.should_report("activity", 5)) {
     		Thread t = Thread.currentThread();
     		Report.report(5, t + "@"+System.currentTimeMillis()
    				+ " terminates with " + tCount + " threads active.");
    		Thread[] a = new Thread[tCount];
    		int count = Thread.enumerate(a);
    		
    		for (int i = 0; i < count; i++) { 
    			Report.report(5, 
    					(a[i].isDaemon() ? "" : "Non") + "Daemon thread " 
    					 + (a[i] == null ? "null" : a[i].getName()) + " is active.");
    		}
    	}
    }
    
    public synchronized void setCurrentPlace(place p) {
        assert p != null;
        Thread t = Thread.currentThread();
        if (t instanceof PoolRunner) {
            PoolRunner pr = (PoolRunner) t;
            pr.place = (LocalPlace_c) p;
        } 
    }
    public synchronized Place currentPlace() {
    	if (places_[0] == null) 
    		initialize();
    	if (places_.length == 1)
    		return places_[0]; // fast path for simple test environments!
    	Thread t = Thread.currentThread();
    	Place ret = null;
    	if (t instanceof PoolRunner) {
    		PoolRunner pr = (PoolRunner) t;
    		ret = pr.place;
    	}
    	if (ret == null)
    		throw new Error("This thread is not an X10 thread!");
    	return ret;
    }
    public Activity currentActivity() {
    	Thread t = Thread.currentThread();
    	Activity result = null;
    	if (t instanceof ActivityRunner) {
    		ActivityRunner pr = (ActivityRunner) t;
    		result = pr.getActivity();
    	} 
    	if (result == null)
    		throw new Error("This thread is not an X10 thread!");
    	return result;
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
    		public dist.factory getDistributionFactory() {
    			return new DistributionFactory();
    		}
    		public point.factory getPointFactory() {    	
    			return new point_c.factory();
    		}
    		public clock.factory getClockFactory() {
    			return new clock.factory() {
    				public clock clock() {
    					return new Clock();
    				}
    				public clock clock(String name) {
    					return new Clock(name);
    				}
    			};
    		}
    		public booleanArray.factory getBooleanArrayFactory() {
                return new BooleanArray.factory() {
                    public BooleanReferenceArray BooleanReferenceArray(dist d, boolean c) {
                        return new BooleanArray_c( d, c, true);
                    }
                    public BooleanReferenceArray BooleanReferenceArray(dist d, booleanArray.pointwiseOp f) {
                        return new BooleanArray_c( d, f, true);
                    }
                    public booleanArray booleanValueArray(dist d, boolean c) {
                        return new BooleanArray_c(d, c, true, false);
                    }
                    public booleanArray booleanValueArray(dist d, booleanArray.pointwiseOp f) {
                        return new BooleanArray_c(d, f, true, false);
                    }
                };
            }
            public charArray.factory getCharArrayFactory() {
                return new CharArray.factory() {
                    public CharReferenceArray CharReferenceArray(dist d, char c) {
                        return new CharArray_c( d, c, true);
                    }
                    public CharReferenceArray CharReferenceArray(dist d, charArray.pointwiseOp f) {
                        return new CharArray_c( d, f, true);
                    }
                    public charArray charValueArray(dist d, char c) {
                        return new CharArray_c(d, c, true, false);
                    }
                    public charArray charValueArray(dist d, charArray.pointwiseOp f) {
                        return new CharArray_c(d, f, true, false);
                    }
                };
            }
            public byteArray.factory getByteArrayFactory() {
                return new ByteArray.factory() {
                    public ByteReferenceArray ByteReferenceArray(dist d, byte c) {
                        return new ByteArray_c( d, c, true);
                    }
                    public ByteReferenceArray ByteReferenceArray(dist d, byteArray.pointwiseOp f) {
                        return new ByteArray_c( d, f, true);
                    }
                    public byteArray byteValueArray(dist d, byte c) {
                        return new ByteArray_c(d, c, true, false);
                    }
                    public byteArray byteValueArray(dist d, byteArray.pointwiseOp f) {
                        return new ByteArray_c(d, f, true, false);
                    }
                };
            }
            public shortArray.factory getShortArrayFactory() {
                return new ShortArray.factory() {
                    public ShortReferenceArray ShortReferenceArray(dist d, short c) {
                        return new ShortArray_c( d, c, true);
                    }
                    public ShortReferenceArray ShortReferenceArray(dist d, shortArray.pointwiseOp f) {
                        return new ShortArray_c( d, f, true);
                    }
                    public shortArray shortValueArray(dist d, short c) {
                        return new ShortArray_c(d, c, true, false);
                    }
                    public shortArray shortValueArray(dist d, shortArray.pointwiseOp f) {
                        return new ShortArray_c(d, f, true, false);
                    }
                };
            }
            public intArray.factory getIntArrayFactory() {
    			return new IntArray.factory() {
    				public IntReferenceArray IntReferenceArray(dist d, int c) {
    					return new IntArray_c( d, c, true);
    				}
    				public IntReferenceArray IntReferenceArray(dist d, intArray.pointwiseOp f) {
    					return new IntArray_c( d, f, true);
    				}
    				public intArray intValueArray(dist d, int c) {
    					return new IntArray_c(d, c, true, false);
    				}
    				public intArray intValueArray(dist d, intArray.pointwiseOp f) {
    					return new IntArray_c(d, f, true, false);
    				}
    			};
    		}
    		public longArray.factory getLongArrayFactory() {
    			return new longArray.factory() {
    				public LongReferenceArray LongReferenceArray(dist d, long c) {
    					return new LongArray_c( d, c, true);
    				}
    				public LongReferenceArray LongReferenceArray(dist d, longArray.pointwiseOp f) {
    					return new LongArray_c( d, f, true);
    				}
    				public longArray longValueArray(dist d, long c) {
    					return new LongArray_c(d, c, true, false);
    				}
    				public longArray longValueArray(dist d, longArray.pointwiseOp f) {
    					return new LongArray_c(d, f, true, false);
    				}
    			};
    		}
    		
            public FloatArray.factory getFloatArrayFactory() {
                return new floatArray.factory() {
                    public FloatReferenceArray FloatReferenceArray(dist d, float c) {
                        return new FloatArray_c( d, c, true);
                    }
                    public FloatReferenceArray FloatReferenceArray(dist d, floatArray.pointwiseOp f) {
                        return new FloatArray_c( d, f, true);
                    }
                    public floatArray floatValueArray(dist d, float c) {
                        return new FloatArray_c(d, c, true, false);
                    }
                    public floatArray floatValueArray(dist d, floatArray.pointwiseOp f) {
                        return new FloatArray_c(d, f, true, false);
                    }
                };              
            }
            
            public DoubleArray.factory getDoubleArrayFactory() {
    			return new doubleArray.factory() {
    				public DoubleReferenceArray DoubleReferenceArray(dist d, double c) {
    					return new DoubleArray_c( d, c, true);
    				}
    				public DoubleReferenceArray DoubleReferenceArray(dist d, doubleArray.pointwiseOp f) {
    					return new DoubleArray_c( d, f, true);
    				}
    				public doubleArray doubleValueArray(dist d, double c) {
    					return new DoubleArray_c(d, c, true, false);
    				}
    				public doubleArray doubleValueArray(dist d, doubleArray.pointwiseOp f) {
    					return new DoubleArray_c(d, f, true, false);
    				}
    			};   			
    		}
            
            public GenericArray.factory getGenericArrayFactory() {
            return new x10.lang.genericArray.factory() {
                public GenericReferenceArray GenericReferenceArray(dist d, Parameter1 c) {
                    return new GenericArray_c( d, c, true);
                }
                public GenericReferenceArray GenericReferenceArray(dist d, x10.lang.genericArray.pointwiseOp f) {
                    return new GenericArray_c( d, f, true);
                }
                public x10.lang.genericArray GenericValueArray(dist d, Parameter1 c) {
                    return new GenericArray_c(d, c, true, false);
                }
                public x10.lang.genericArray GenericValueArray(dist d, x10.lang.genericArray.pointwiseOp f) {
                    return new GenericArray_c(d, f, true, false);
                }
            };
            
        }

            
            public place.factory getPlaceFactory() {
    			return new place.factory() {
    				public place place(int i ) {
    					
    					int index =( i %  place.MAX_PLACES);
    					//System.out.println("place(i), i= " + i + " index=" +  index + " place.MAX_PLACES" + place.MAX_PLACES);
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
     * At which place is the given activity running?  
     * 
     * @param a 
     * @return null if the activity is not running anywhere
     */
    public synchronized Place getPlaceOfActivity(Activity a) {
           return a.place_;
    }    

    static class Signal { boolean value; }
    
} // end of DefaultRuntime_c
