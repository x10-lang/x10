package x10.runtime;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import x10.core.Ref;
import x10.runtime.util.ConfigurationError;
import x10.runtime.util.ShowUsageNotification;

public abstract class Runtime {
        public static Runtime runtime;

        static JavaRuntime java;

	public static void main(String[] args) {
	        if (args.length == 0) {
	            System.err.println("usage: java x10.runtime.Runtime MainClass [args]");
	            System.exit(1);
	        }
	        
                try {
                      String[] strippedArgs = Configuration.parseCommandLine(args);
                      init();
                      runtime.run(strippedArgs);
                }
                catch (ShowUsageNotification e) {
                        Usage.usage(System.out, null);
                }
                catch (ConfigurationError e) {
                        Usage.usage(System.err, e);
                }
                catch (Exception e) {
                        Runtime.java.error("Unexpected Exception in X10 runtime.", e);
                }
	}

	protected abstract void run(String[] args);

        private static boolean done_;

        public static void init() {
                assert !done_;
                done_ = true;
                String rt = System.getProperty("x10.runtime");
                Runtime r = null;
                if (rt == null)
                        rt = "x10.runtime.DefaultRuntime";
                try {
                    r = (Runtime) Class.forName(rt).newInstance();
                }
                catch (ClassNotFoundException cnfe) {
                    System.err.println("Runtime::<clinit> did not find runtime " + rt);
                    throw new Error(cnfe);
                }
                catch (IllegalAccessException iae) {
                    System.err.println("Runtime::<clinit> could not access runtime " + rt);
                    throw new Error(iae);
                }
                catch (InstantiationException ie) {
                    System.err.println("Runtime::<clinit> could not create runtime " + rt);
                    throw new Error(ie);
                }
                catch (Throwable t) {
                    System.err.println("Runtime::<clinit> unknown exception during creation of runtime " + rt);
                    throw new Error(t);
                }
                finally {
                    assert r != null;
                    runtime = r;
                    java = new JavaRuntime();
                }
        }

    /**
     * Utility method to print a number of spaces to a PrintStream.
     * @param out output PrintStream
     * @param n number of spaces to print.
     */
    protected static void printSpaces(PrintStream out, int n) {
        while (n-- > 0) {
            out.print(' ');
        }
    }

        protected abstract void initialize();

	public abstract Place currentPlace();
	
	public static Place here() {
            return runtime.currentPlace();
	}
	
	public static <T> T hereCheck(T o) {
            if (Configuration.BAD_PLACE_RUNTIME_CHECK && o != null) {
                hereCheckPlace(location(o));
            }
            return o;
	}
	
	public static void hereCheckPlace(Place p) {
            if (p != ((PoolRunner) Thread.currentThread()).getPlace())
                throw new BadPlaceException(p, here());
	}
	
        public static java.lang.Object placeCheck(Object o, Place p) {
                if (o == null)
                        throw new ClassCastException("Place-cast of value 'null' failed.");
                if (! location(o).equals(p))
                        throw new BadPlaceException(o, here()); // (o, p)?
                return o;
        }

        public static Activity getCurrentActivity() {
                return runtime.currentActivity();
        }
        
        public abstract Activity currentActivity();

	public static Place location(Object o) {
	    if (o instanceof Ref) {
	        return ((Ref) o).location();
	    }
	    return here();
	}
	
	public static Place asPlace(Object o) {
	    if (o instanceof Place)
	        return (Place) o;
	    return location(o);
	}
	        
	protected abstract Place[] getPlaces();
	
	public static Place getDefaultPlace() {
            return runtime.getPlaces()[0];
	}
	
	public static Place place(int i) {
	    return runtime.getPlaces()[i];
	}
	
	public static Place[] places() {
	    return runtime.getPlaces();
	}
	public static Set<Place> placeSet() {
	    return new TreeSet<Place>(Arrays.asList(places()));
	}
	
	public static Place FIRST_PLACE;
	public static int MAX_PLACES;
	
	public abstract	void setCurrentPlace(Place p);

        public abstract void prepareForBoot();

        public abstract void shutdown();

        static int exitCode;

        public static void setExitCode(int code) {
                exitCode = code;
        }

        public static void x10Exit() {
                if (Report.should_report("activity", 3)) {
                        Thread t = Thread.currentThread();
                        Report.report(3, t + "@" + System.currentTimeMillis()
                                        + " The XVM is now terminating.");
                }
                System.exit(exitCode);
        }

        public static void exit(int code) {
                setExitCode(code);
                x10Exit();
        }

        /**
         * Sleep for the specified number of milliseconds.
         * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
         * @param millis the number of milliseconds to sleep
         * @return true if completed normally, false if interrupted
         */
        public static boolean sleep(long millis) {
                Thread th = Thread.currentThread();
                PoolRunner activityRunner = null;

                // NOTE: some "legacy" applications (e.g., the test harness)
                // may call this method from a java.lang.Thread.
                if (th instanceof PoolRunner) {
                     activityRunner = (PoolRunner) th;
                }
                
                try {
                    // Notify runtime that thread executing current activity will be blocked
                    if (activityRunner != null)
                        activityRunner.getPlace().threadBlockedNotification();
                    Thread.sleep(millis);
                    return true;
                } catch (InterruptedException e) {
                    return false;
                } finally {
                    // Notify runtime that thread executing current activity has become unblocked   
                    if (activityRunner != null)
                        activityRunner.getPlace().threadUnblockedNotification();
                }
        }

}
