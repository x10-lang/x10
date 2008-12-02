/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

import x10.array.ArrayFactory;
import x10.compilergenerated.Parameter1;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Future_c;
import x10.runtime.Place;
import x10.runtime.PoolRunner;
import x10.runtime.Report;
import x10.runtime.util.ConfigurationError;
import x10.runtime.util.ShowUsageNotification;

/**
 * This is the central entrypoint to the X10 Runtime for the
 * compiler. There is exactly one Runtime per JVM running X10.
 * 
 * The Runtime is NOT an X10Object! In fact, it cannot be since
 * X10Object's constructor requires already an existing and working
 * Runtime.  Conceptually Runtime is an Interface (!) for the X10
 * world (just like Clock/Region/Distribution/Array/Range/Activity).
 * It is implemented as an abstract class since we need to put some
 * code here, notably the static  "getRuntime()"
 * implementation.  Sadly, Java does not allow statics code in
 * interfaces, otherwise this would be an interface. 
 * 
 * @author Christian Grothoff, Christoph von Praun
 * @see Place
 * @see Activity
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: add runBootAsync() method, which is like runAsync() except that it is only used for the boot activity
 */
public abstract class Runtime {

	public static Runtime runtime;

	/**
	 * This instance should be used only in the implementation of 
	 * the x10.runtime. 
	 */
	public static JavaRuntime java;

	private static boolean done_;

	public static void init() {
		assert !done_;
		done_ = true;
		String rt = System.getProperty("x10.runtime");
		Runtime r = null;
		try {
			if (rt != null)
				r = (Runtime) Class.forName(rt).newInstance();
			else
				r = new DefaultRuntime_c();
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Runtime::<clinit> did not find runtime " + rt);
			throw new Error(cnfe);
		} catch (IllegalAccessException iae) {
			System.err.println("Runtime::<clinit> could not access runtime "
					+ rt);
			throw new Error(iae);
		} catch (InstantiationException ie) {
			System.err.println("Runtime::<clinit> could not create runtime "
					+ rt);
			throw new Error(ie);
		} catch (Throwable t) {
			System.err
					.println("Runtime::<clinit> unknown exception during creation of runtime "
							+ rt);
			throw new Error(t);
		} finally {
			assert (r != null);
			runtime = r;
			java = new JavaRuntime();
			factory = runtime.getFactory();
			// ArrayFactory.init(r);
		}
	}

	public static Factory factory;

	protected abstract void initialize();

	public static void main(String[] args) {
		try {
			String[] args_stripped = Configuration.parseCommandLine(args);
			init();
			runtime.run(args_stripped);
		} catch (ShowUsageNotification e) {
			usage(System.out, null);
		} catch (ConfigurationError e) {
			usage(System.err, e);
		} catch (Exception e) {
			Runtime.java.error("Unexpected Exception in X10 Runtime.", e);
		}
		// vj: The return from this method does not signal termination of the VM
		// because a separate non-daemon thread has been spawned to execute Boot Activity.
	}

	/**
	 * The name of the language this runtime represents.
	 */
	public static final String LANGUAGE = "x10";

	/**
	 * Print usage information
	 */
	public static void usage(PrintStream out, ConfigurationError err) {
		if (err != null)
			out.println("Error: "+err.getMessage());
		out.println("Usage: " + LANGUAGE + " [options] " +
				"<main-class> [arg0 arg1 ...]");
		out.println("where [options] includes:");
		String[][] options = Configuration.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}

	/* The following code was borrowed from Polyglot's Options.java */
    /**
     * The maximum width of a line when printing usage information. Used
     * by <code>usageForFlag</code> and <code>usageSubsection</code>.
     */
    protected static final int USAGE_SCREEN_WIDTH = 76;

    /**
     * The number of spaces from the left that the descriptions for flags will
	 * be displayed. Used by <code>usageForFlag</code>.
     */
    private static final int USAGE_FLAG_WIDTH = 34;

    /**
     * The number of spaces from the left that the flag names will be
	 * indented. Used by <code>usageForFlag</code>.
     */
    private static final int USAGE_FLAG_INDENT = 4;

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

    /**
     * Output a flag and a description of its usage in a nice format.
     *
     * @param out output PrintStream
     * @param flag
     * @param description description of the flag.
     */
    protected static void usageForFlag(PrintStream out, String flag, String description) {
		printSpaces(out, USAGE_FLAG_INDENT);
        out.print(flag);
        // cur is where the cursor is on the screen.
        int cur = flag.length() + USAGE_FLAG_INDENT;

        // print space to get up to indentation level
        if (cur < USAGE_FLAG_WIDTH) {
            printSpaces(out, USAGE_FLAG_WIDTH - cur);
        }
        else {
            // the flag is long. Get a new line before printing the
            // description.
            out.println();
            printSpaces(out, USAGE_FLAG_WIDTH);
        }
        cur = USAGE_FLAG_WIDTH;

        // break up the description.
        StringTokenizer st = new StringTokenizer(description);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (cur + s.length() > USAGE_SCREEN_WIDTH) {
                out.println();
                printSpaces(out, USAGE_FLAG_WIDTH);
                cur = USAGE_FLAG_WIDTH;
            }
            out.print(s);
            cur += s.length();
            if (st.hasMoreTokens()) {
                if (cur + 1 > USAGE_SCREEN_WIDTH) {
                    out.println();
                    printSpaces(out, USAGE_FLAG_WIDTH);
                    cur = USAGE_FLAG_WIDTH;
                }
                else {
                    out.print(" ");
                    cur++;
                }
            }
        }
        out.println();
    }

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
		PoolRunner activityRunner = (PoolRunner) Thread.currentThread();
		try {
			// Notify runtime that thread executing current activity will be blocked
			activityRunner.getPlace().threadBlockedNotification();
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			return false;
		} finally {
			// Notify runtime that thread executing current activity has become unblocked
			activityRunner.getPlace().threadUnblockedNotification();
		}
	}

	protected Runtime() {
	}

	public static abstract class Factory {
		public abstract region.factory getRegionFactory();

		public abstract dist.factory getDistributionFactory();

		public abstract point.factory getPointFactory();

		public abstract clock.factory getClockFactory();

		public abstract place.factory getPlaceFactory();

		public abstract ArrayFactory getArrayFactory();
	}

	public abstract Factory getFactory();

	/**
	 * Run the X10 application.
	 */
	protected abstract void run(String[] args) throws Exception;

	public abstract void setCurrentPlace(place p);

	public abstract Place currentPlace();

	public abstract Activity currentActivity();

	protected abstract Place[] getPlaces();

	public abstract void prepareForBoot();

	public abstract void shutdown();

	public abstract void run(final Activity appMain);

	/**
	 * A specialized method for copying a portion of an array from one object located 
	 * here to another linked object located at the given remote place. The two objects
	 * are linked in that are pointed to by G[here.id] and G[remote.id]. 
	 * 
	 * The objects must be of type x10.lang.RemoteDoubleArrayCopier.
	 * 
	 * @param G  -- the global array (i.e. defined uniquely over all places)
	 * @param srcoffset -- the offset in the source array from which to copy
	 * @param remote   -- the remote place identifying the remote object
	 * @param destoffset -- the offset in the target array into which to copy
	 * @param length  -- the number of elements to be copied.
	 * @param postCopyRun -- if true, the destination objects postCopyRun() method is called after copying.
	 */
	public static void asyncDoubleArrayCopy(genericArray G, int srcoffset, 
			place remote, int destoffset, int length, boolean postCopyRun) {
		assert G != null;
		Parameter1[] GB = G.getBackingArray();
		
		place here = here();
		assert here.id < GB.length;
		assert remote.id < GB.length;
		Parameter1 oh = G.get(here.id);
		assert oh != null;
		assert oh instanceof RemoteDoubleArrayCopier;
		RemoteDoubleArrayCopier src = (RemoteDoubleArrayCopier) oh;
		RemoteDoubleArrayCopier dest = (RemoteDoubleArrayCopier) G.get(remote.id);
		assert dest != null;
		double[] destArray = dest.getDestArray().getBackingArray();
		double[] srcArray = src.getSourceArray().getBackingArray();
		try {
			System.arraycopy(srcArray, srcoffset, destArray, destoffset, length);
		} catch (ArrayStoreException z) {
			z.printStackTrace();
			throw z;
		}
		if (postCopyRun) {
			try {
				runtime.setCurrentPlace(remote);
				dest.postCopyRun(destoffset);
			} finally {
				runtime.setCurrentPlace(here);
			}
		}
	}
	/**
	 * Low-level copy operation.  Use at your own risk.
	 */
	public static void arrayCopy(x10Array src, int srcoffset, x10Array dest, int destoffset, int length) {
		runtime.arrayCopy_internal(src, srcoffset, dest, destoffset, length);
	}
	public static void arrayGet(x10Array src, int srcoffset, x10Array dest, int destoffset, int length) {
		arrayCopy(src, srcoffset, dest, destoffset, length);
	}
	public static void arrayPut(x10Array src, int srcoffset, x10Array dest, int destoffset, int length) {
		arrayCopy(src, srcoffset, dest, destoffset, length);
	}
	
	/**
	 * Copy the i'th element in the enumeration order of the source array into the
	 * i'th element in the enumeration order of the target array. The two arrays must
	 * be of the same size.
	 * @param src
	 * @param dest
	 */
	public static void arrayCopy(x10Array src, x10Array dest) {
		//if (src.distribution.rank != dest.distribution.rank)
		//	throw new RankMismatchException(src.distribution.rank, dest.distribution.rank);
		if (src.distribution.region.size() != dest.distribution.region.size())
			throw new IllegalArgumentException("Arrays must be of the same size");
		arrayCopy(src, 0, dest, 0, src.distribution.region.size());
	}
	
	public static void arrayCopyAdd(DoubleReferenceArray src, int srcoffset, DoubleReferenceArray dest,
			int destoffset, int length) {
		//if (src.distribution.rank != dest.distribution.rank)
		//	throw new RankMismatchException(src.distribution.rank, dest.distribution.rank);
		if (src.distribution.region.size() != dest.distribution.region.size())
			throw new IllegalArgumentException("Arrays must be of the same size");
		runtime.arrayCopyAdd_internal(src, srcoffset, dest, destoffset, length);
	}

	protected abstract void arrayCopyAdd_internal(DoubleReferenceArray src, int srcoffset, DoubleReferenceArray dest, int destoffset, int length);
	protected abstract void arrayCopy_internal(x10Array src, int srcoffset, x10Array dest, int destoffset, int length);

	/**
	 * @return The place of the thread executing the current activity. 
	 * ('here' in X10).
	 */
	public static Place here() {
		place p = runtime.currentPlace();
		
		assert  p != null;
		return (Place) p;
	}

	/**
	 * @return The given place if non-null, or 'here'.
	 */
	public static place asPlace(place p) {
		return p == null ? here() : p;
	}

	/* this is called from inside the array library */
	public static void hereCheckPlace(place p) {
		if (p != ((PoolRunner) Thread.currentThread()).getPlace())
			throw new BadPlaceException(p, here());
	}

	/* this is called from the code snippet for field and array access */
	public static java.lang.Object hereCheck(java.lang.Object o) {
		if (Configuration.BAD_PLACE_RUNTIME_CHECK && o != null
				&& o instanceof x10.lang.Object && !(o instanceof ValueType)) {
			hereCheckPlace(((x10.lang.Object) o).getLocation());
		}
		return o;
	}

	/**
	 * Method used to do dynamic nullcheck when nullable is casted away.
	 */
	public static java.lang.Object placeCheck(java.lang.Object o,
			x10.lang.place p) {
		if (o == null)
			throw new ClassCastException("Place-cast of value 'null' failed.");
		if (!(o instanceof x10.lang.Object))
			throw new Error(
					"Place-cast currently not available for object of type "
							+ o.getClass().getName());
		x10.lang.Object xo = (x10.lang.Object) o;
		if (!xo.getLocation().equals(p))
			throw new BadPlaceException(xo, here());
		return o;
	}

	public static Activity getCurrentActivity() {
		return runtime.currentActivity();
	}

	public static Place[] places() {
		Place[] pl = runtime.getPlaces();
		Place[] ret = new Place[pl.length];
		System.arraycopy(pl, 0, ret, 0, pl.length);
		return ret;
	}
	
	public static Place getDefaultPlace()
	{
		return runtime.getPlaces()[0];
	}

	
	/**
	 * @deprecated
	 */
	public static void runAsync(final Activity a) {
		runtime.getPlaces()[0].runAsync(a);
	}
	
	/**
	 * @deprecated
	 */
	public static Future runFuture(Future_c.Activity a) {
		return runtime.getPlaces()[0].runFuture(a);
	}

	/**
	 * Implementation of "==" for value types.
	 * TODO: vj, implement for value arrays.
	 * @param o1
	 * @param o2
	 * @return true iff the values are value-equals (all fields have
	 *    the same value)
	 */
	public static boolean equalsequals(java.lang.Object o1, java.lang.Object o2) {
		if (o1 == o2)
			return true;
		if ((o1 == null) || (o2 == null))
			return false;
		Class c = o1.getClass();
		if ((o1 instanceof Indexable) && (o2 instanceof Indexable)) {
			Indexable i1 = (Indexable) o1;
			Indexable i2 = (Indexable) o2;
			if (!(i1.isValue() && i2.isValue()))
				return false;
			return i1.valueEquals(i2);
		}
		if (c != o2.getClass())
			return false;
		if (!(o1 instanceof ValueType))
			return false;
		try {
			while (c != null) {
				Field[] fs = c.getDeclaredFields();
				for (int i = fs.length - 1; i >= 0; i--) {
					Field f = fs[i];
					if (Modifier.isStatic(f.getModifiers()))
						continue;
					f.setAccessible(true);
					if (f.getType().isPrimitive()) {
						if (!f.get(o1).equals(f.get(o2)))
							return false;
					} else if (f.getType().isArray()) {
						java.lang.Object a1 = f.get(o1);
						java.lang.Object a2 = f.get(o2);
						int len = Array.getLength(a1);
						if (len != Array.getLength(a2))
							return false;
						for (int j = 0; j < len; j++)
							if (!Array.get(a1, j).equals(Array.get(a2, j)))
								return false;
					} else {
						// I assume here that value types are immutable
						// and can thus not contain mutually recursive
						// structures.  If that is wrong, we would have to do
						// more work here to avoid dying with a StackOverflow.
						if (!equalsequals(f.get(o1), f.get(o2)))
							return false;
					}
				}
				c = c.getSuperclass();
				if ((c == java.lang.Object.class)
						|| (c == x10.lang.Object.class))
					break; // otherwise we get problems with fields like 'place' in X10Object
			}
		} catch (IllegalAccessException iae) {
			throw new Error(iae); // fatal, should never happen
		}
		return true;
	}

} // end of Runtime
