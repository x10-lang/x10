package x10.runtime;

import java.util.Set;

import java.util.TreeSet;


import x10.array.DoubleArray;
import x10.array.FloatArray;
import x10.array.GenericArray;
import x10.array.IntArray;
import x10.array.StructureArray;
import x10.array.BooleanArray;
import x10.array.CharArray;
import x10.array.ByteArray;
import x10.array.ShortArray;
import x10.array.point_c;
import x10.array.DistributionFactory;
import x10.array.sharedmemory.DoubleArray_c;
import x10.array.sharedmemory.FloatArray_c;
import x10.array.sharedmemory.GenericArray_c;
import x10.array.sharedmemory.BooleanArray_c;
import x10.array.sharedmemory.CharArray_c;
import x10.array.sharedmemory.ByteArray_c;
import x10.array.sharedmemory.ShortArray_c;
import x10.array.sharedmemory.IntArray_c;
import x10.array.sharedmemory.LongArray_c;
import x10.array.sharedmemory.StructureArray_c;
import x10.array.sharedmemory.Complex4Array_c;
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
import x10.lang.StructureReferenceArray;
import x10.lang.Complex4ReferenceArray;
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
import x10.lang.structureArray;
import x10.lang.complex4Array;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.distributed.RemotePlace;

/**
 * Default implementation of Runtime. Considerably revised 5/16 by vj
 * to remove activity helper classes, and synchronization between
 * main thread and the Boot Activity.
 *
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 *
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use new getPlace/setPlace interfaces in PoolRunner.
 * Also call runBootAsync() instead of runAsync() for boot activity.
 */
public class DefaultRuntime_c extends Runtime {

	/**
	 * The places of this X10 Runtime (for now a constant set).
	 */
	protected /*final*/ Place[] places_;

	public DefaultRuntime_c() {
		this.places_ = new Place[Configuration.NUMBER_OF_LOCAL_PLACES];
	}

	/**
	 * Initialize the places in the XVM.
	 */
	protected synchronized void createPlaces() {
		int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
		x10.lang.place.MAX_PLACES = pc;
		for (int i=0;i<pc;i++)
			places_[i] = new LocalPlace_c();
		place.initialize();
	}

	protected synchronized void initialize() {
		// do it only once
		if (places_[0] == null) {
			createPlaces();
		}
	}

	protected synchronized void loadAndInitLibs() {
		if (null != Configuration.LOAD) {
			String[] libs = Configuration.LOAD.split(":");
			for (int i=libs.length-1;i>=0;i--)
				System.loadLibrary(libs[i]);
		}
	}

	protected void finalizeAndTermLibs() {
	}

	private static final Class[] STRING_ARRAYS = new Class[] { String[].class };
	/**
	 * Run the X10 application.
	 */
	protected void run(String[] args) {

		if (Report.should_report("activity", 5)) {
			Thread t = Thread.currentThread();
			int tCount = Thread.activeCount();
			Report.report(5, Thread.currentThread() + ":" + System.currentTimeMillis() +" starts in group " + t.getThreadGroup()
			              + " with " + tCount + " threads active.");
			Thread[] a = new Thread[tCount];
			int count = Thread.enumerate(a);

			for (int i = 0; i < count; i++) {
				Report.report(5, "Thread " + (a[i] == null ? "null" : a[i].getName()) + " is active.");
			}
		}
		// first: load libraries!
		loadAndInitLibs();
//		// then: initialize the runtime
//		initialize();

		// Find the applications main activity.
		java.lang.Object[] tmp = { args };
		Activity atmp = null;
		try {
			if (Report.should_report("activity", 5)) {
				Report.report(5, Thread.currentThread()  + ":" + System.currentTimeMillis() + " " + this + " starting user class |"
				              + Configuration.MAIN_CLASS_NAME+ "|.");
			}
			Class main = Class.forName(Configuration.MAIN_CLASS_NAME + "$Main");
			if (Configuration.PRELOAD_CLASSES)
				PreLoader.preLoad(main, Configuration.PRELOAD_STRINGS);
			atmp = (Activity) main.getDeclaredConstructor(STRING_ARRAYS).newInstance(tmp);
		} catch (Exception e) {
			System.err.println("Could not find default constructor of main class '"
			                   + Configuration.MAIN_CLASS_NAME+ "$Main" + "'!");
			throw new Error(e);
		}
		final Activity appMain = atmp;

		// Create the boot activity
		Activity boot = new Activity() {
			public String myName() {
				return "Boot activity";
			}
			public void run() {
				if (Report.should_report("activity", 5)) {
					Report.report(5, PoolRunner.logString() + " starts running the Boot Activity.");
				}
				try {
					finishRun(appMain);
				} catch (Throwable z) {
					// Exception thrown by the activity!
					z.printStackTrace();
				}

				if (Report.should_report("activity", 5)) {
					Report.report(5, PoolRunner.logString() + " finishes running the Boot Activity.");

				}

				// and now the shutdown sequence!
				// places_[] should have been initialized by now ...
				for (int i=getPlaces().length-1; i >= 0;i--) {
					if (Report.should_report("activity", 5)) {
						Report.report(5, PoolRunner.logString() + " shutting down " + getPlaces()[i]);

					}
					getPlaces()[i].shutdown();
				}

				if (Report.should_report("activity", 5)) {
					Report.report(5, PoolRunner.logString() + " terminates.");

				}
				// The VM goes bye bye.
				finalizeAndTermLibs();
				Runtime.x10Exit();
			}
		};

		// Run the boot activity.
		if (Configuration.runBootHere) {
			Runtime.runBootAsync(boot);
		}

		finalizeAndTermLibs();

		//
		// Main thread terminates. bootActivity will now carry on.
		// VM terminates when bootActivity terminates.
		return;
	}

	/**
	 * Set the current place in the PoolRunner. Should only
	 * be used by the X10 runtime with care. In general
	 * the X10 runtime should use currentActivity().getPlace().
	 * @see currentPlace()
	 */
	public synchronized void setCurrentPlace(place p) {
		assert p != null;
		Thread t = Thread.currentThread();
		if (t instanceof PoolRunner)
			((PoolRunner)t).setPlace((Place)p);
	}

	/**
	 * The place at which the current Thread is running, as recorded in the PoolRunner.
	 * Is different from currentActivity().getPlace() only in very special circumstances,
	 * for instance during the execution of the body of an array initializer.
	 * @see setCurrentPlace(place)
	 */
	public synchronized Place currentPlace() {
		if (getPlaces()[0] == null)
			initialize();
		if (getPlaces().length == 1)
			return getPlaces()[0]; // fast path for simple test environments!
		Thread t = Thread.currentThread();
		Place ret = null;
		if (t instanceof PoolRunner)
			ret=(Place)((PoolRunner)t).getPlace();

		/* TODO:  when entering from LAPI (another VM) there may
		   not be a "currentPlace"  Find a better test than this one. */
		if (Configuration.VM_ == null && ret == null)
			throw new Error("This thread is not an X10 thread!");
		return ret;
	}

	/**
	 * Return the activity being executed by the current thread.
	 */
	public Activity currentActivity() {
		Thread t = Thread.currentThread();
		Activity result = null;
		if(t instanceof ActivityRunner) {
			result = ((ActivityRunner)t).getActivity();
		}

		/* TODO:  when entering from LAPI (another VM) there may
		   not be a "currentActivity"  Find a better test than this one. */
		if (Configuration.VM_ == null && result == null)
			throw new Error("This thread is not an X10 thread!");
		return result;
	}

	/**
	 * Should be used only internally to the XVM. Should not
	 * be exposed to the X10 programmer.
	 *  @see x10.lang.place
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

	protected Place[] getLocalPlaces() {
		return getPlaces();
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
					public intArray intValueArray(int[] a) {
						return IntArray_c.IntArray_c(a, true, false);
					}
				};
			}
			public StructureArray.factory getStructureArrayFactory() {
				return new StructureArray.factory() {
					public StructureReferenceArray StructureReferenceArray(dist d, int c,int elSize) {
						return new StructureArray_c( d, c, true,elSize);
					}
					public StructureReferenceArray StructureReferenceArray(dist d, structureArray.pointwiseOp f,int elSize) {
						return new StructureArray_c( d, f, true,elSize);
					}
					public structureArray structureValueArray(dist d, int c,int elSize) {
						return new StructureArray_c(d, c, true, false,elSize);
					}
					public structureArray structureValueArray(dist d, structureArray.pointwiseOp f,int elSize) {
						return new StructureArray_c(d, f, true, false,elSize);
					}
				};
			}
			public complex4Array.factory getComplex4ArrayFactory() {
				return new complex4Array.factory() {
					public Complex4ReferenceArray Complex4ReferenceArray(dist d, float c) {
						return new Complex4Array_c( d, c, true);
					}
					public Complex4ReferenceArray Complex4ReferenceArray(dist d, complex4Array.pointwiseOp f) {
						return new Complex4Array_c( d, f, true);
					}
					public complex4Array complex4ValueArray(dist d, float c) {
						return new Complex4Array_c(d, c, true, false);
					}
					public complex4Array complex4ValueArray(dist d, complex4Array.pointwiseOp f) {
						return new Complex4Array_c(d, f, true, false);
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
					public doubleArray doubleValueArray(double[] a) {
						return DoubleArray_c.DoubleArray_c(a, true, false);
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
					public x10.lang.genericArray GenericValueArray(dist d, Parameter1 c, boolean refs_to_values) {
						return new GenericArray_c(d, c, true, false, refs_to_values);
					}
					public x10.lang.genericArray GenericValueArray(dist d, x10.lang.genericArray.pointwiseOp f, boolean refs_to_values) {
						return new GenericArray_c(d, f, true, false, refs_to_values);
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
					/**
					 * Return the set of places from place(0) to place(last) (inclusive).
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
} // end of DefaultRuntime_c

