package x10.runtime;

import java.util.ArrayList;
import java.util.Hashtable;

import x10.array.IntArray;
import x10.lang.Future;
import x10.lang.dist;
import x10.lang.point;
import x10.lang.region;
import x10.lang.x10Array;
import x10.runtime.distributed.AsyncResult;
import x10.runtime.distributed.FatPointer;


/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 * @author vj
 */
public class LocalPlace_c extends Place {
	
	
	/**
	 * Compute the number of simulated cycles spent
	 * globally at this point.
	 * 
	 * this method must not be called in the constructor of 
	 * LocalPlace_c, and not before the runtime is initialized 
	 * completely.
	 *  
	 * @return max of all simulatedPlaceTimes of all places
	 */
	public static long systemNow() {
		long max = 0;
		Place[] places = x10.lang.Runtime.places();
		for (int i=places.length-1;i>=0;i--) {
			long val = 0;
			if (places[i] instanceof LocalPlace_c) {
				val = ((LocalPlace_c) places[i]).getSimulatedPlaceTime();
			}
			if (val > max)
				max = val;
		}
		return max;
	}
	
	public static void initAllPlaceTimes(Place[] places) {
		long max = 0;
		for (int i=places.length-1;i>=0;i--) {
			assert (places[i] instanceof LocalPlace_c);
			long val = 0;
			LocalPlace_c lp = (LocalPlace_c) places[i];
			val = lp.getSimulatedPlaceTime();
			if (val > max)
				max = val;
		}
		for (int i=places.length-1;i>=0;i--) {
			LocalPlace_c lp = (LocalPlace_c) places[i];
			lp.startBlock = max; 
		}
	}
	
	
	/**
	 * Is this place shutdown?
	 */
	boolean shutdown;
	
	/**
	 * How many threads are truely running (not blocked) in this place?
	 */
	int runningThreads;
	
	/**
	 * Linked list of threads in the thread pool that are not currently
	 * assigned to an Activity.  Package scoped to allow sampling.
	 */
	PoolRunner threadQueue_;
	
	/**
	 * List of all of the threads of this place.
	 */
	final ArrayList myThreads = new ArrayList(); // <PoolRunner>
	
	/**
	 * The amount of cycles that this places was blocked waiting
	 * for activities at other places to complete.  
	 */
	long blockedTime;
	
	/**
	 * "global" time at which this place was blocked (that is, all
	 * activities at this place were blocked).
	 */
	private long startBlock; 
	private static Hashtable _fatPointerMap;
	LocalPlace_c(int vm_, int place_no_) {
		super(vm_, place_no_);
		_fatPointerMap = new Hashtable();
	}
	
	/**
	 * Get how many cycles were spent in computation or blocked at this 
	 * place so far.  Only (sort of) works on JikesRVM where we can get
	 * per-thread cycle counts.
	 *
	 * @return
	 */
	public long getSimulatedPlaceTime() {
		long ret = blockedTime;
		synchronized (myThreads) {
			for (int i = myThreads.size()-1;i>=0;i--)
				ret += ((PoolRunner)myThreads.get(i)).getThreadRunTime();
		}
		return ret;
	}
	
	synchronized void addThread( PoolRunner p) {
		synchronized (myThreads) { myThreads.add(p); }
	}
	/**
	 * Shutdown this place, the current X10 runtime will exit.    Assumes
	 * that all activities have already completed.  Threads beloging
	 * to activities that are not done at this point will be left
	 * running (which is probably a good policy, least for the thread
	 * that calls shutdown :-).
	 */
	public synchronized void shutdown() {
		synchronized (this) {
			shutdown = true;
			if (Report.should_report("activity", 5)) {
				Report.report(5, PoolRunner.logString() + " shutting down " + threadQueue_);
				PoolRunner list = threadQueue_;
				while (list != null) {
					if (Report.should_report("activity", 7)) {
						Report.report(7, Thread.currentThread() +  "@" + list.place + ":" + System.currentTimeMillis() 
								+"  threadpool contains " + list);
					}
					list = list.next;
				}
			}
		}
		while (this.threadQueue_ != null) {
			if (Report.should_report("activity", 5)) {
				Report.report(5, PoolRunner.logString() + " shutting down " + threadQueue_);
			}
			
			threadQueue_.shutdown();
			
			try {
				threadQueue_.join();
				if (Report.should_report("activity", 5)) {
					Report.report(5, PoolRunner.logString() + " " + threadQueue_ + " shut down.");
				}
			} catch (InterruptedException ie) {
				throw new Error(ie); // should never happen...
			}
			
			threadQueue_ = threadQueue_.next;
		}
	}
	
	boolean isShutdown() { 
		return shutdown; 
	}
	/**
	 * Run the given activity asynchronously.
	 * vj 5/17/05. This has been completely revamped,
	 * with Activity given much more responsibility for its execution.
	 */
	public void runAsync(final Activity a) {
		if (a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
				a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate) {
			runAsync( a, false);
		} else {
			a.pseudoDeSerialize();
			runAsync( a, true);
		}
	}
	/**
	 * Run this activity asynchronously, as if it is wrapped in a finish.
	 * That is, wait for its global termination.
	 * @param a
	 */
	public void finishAsync( final Activity a) {
		runAsync( a, true);
	}
	protected void runAsync(final Activity a, final boolean finish) {
		
		Thread currentThread = Thread.currentThread();  
		if (currentThread instanceof ActivityRunner && a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal) {
			// This will not be executed only for bootActivity.
			Activity parent = ((ActivityRunner) currentThread).getActivity();
			parent.finalizeActivitySpawn(a);
		}
		
		a.initializeActivity();
		this.execute(new Runnable() {
			public void run() {
				// Get a thread to run this activity.
				PoolRunner t = (PoolRunner) Thread.currentThread();
				if (Report.should_report("activity", 5)) {
					Report.report(5, t + " is running " + this);
				}
				
				// Install the activity.
				t.setActivity(a);
				a.setPlace(LocalPlace_c.this);
				
				try {
					if (finish) {
						a.finishRun();
					} else {
						a.run();
					}
				} catch (Throwable e) {
					a.finalizeTermination(e);
					return;
				}
				a.finalizeTermination(); //should not throw an exception.
			}
			public String toString() { return "<Executor " + a+">";}
			
		});
	}
	
	/**
	 * Run the given activity asynchronously.  Return a handle that
	 * can be used to force the future result.
	 */
	public Future runFuture(final Activity.Expr a) {
		Future_c result = a.future = new Future_c();
		runAsync(a);
		return result;
	}
	
	/**
	 * Run the given Runnable using one of the threads in the thread pool.
	 * Note that the activity is guaranteed to be assigned a thread 
	 * right away, the pool can never be exhausted (the size is infinite).
	 * 
	 * @param r the activity to run
	 * @throws InterruptedException
	 */
	protected void execute(Runnable r) {
		PoolRunner t;
		synchronized(this) {
			if (threadQueue_ == null) {
				t = new PoolRunner(this);
				t.setDaemon(false);
				t.start();
				if (Report.should_report("activity", 5)) {
					Report.report(5, PoolRunner.logString() + "LocalPlace starts " 
							+ (t.isDaemon() ? "" : "non") + "daemon thread " + t 
							+ "in group " + Thread.currentThread().getThreadGroup() 
							+".");
				}
				
			} else {
				t = threadQueue_;
				threadQueue_ = t.next;
			}
		}
		t.run(r);
	}
	
	/**
	 * Method called by a PoolRunner to add a thread back
	 * to the thread pool (the PoolRunner is done running
	 * the job).
	 * 
	 * @param r
	 */
	/*package*/ synchronized final void repool(PoolRunner r) {
		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString() + " repools (shutdown=" + shutdown + ").");
		}
		
		r.next = threadQueue_;
		threadQueue_ = r;
	}
	
	
	/**
	 * Change the 'running' status of a thread.
	 * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
	 */
	/*package*/ synchronized void changeRunningStatus(int delta) {
		if (runningThreads == 0) {
			assert delta > 0;
			this.blockedTime += systemNow() - startBlock;
		}
		runningThreads += delta;
		if (runningThreads == 0) {
			assert delta < 0;
			startBlock = systemNow();
		}
	}
	
	public String longName() {
		return this.toString() + "(shutdown="+shutdown+")";
	}
	
	public static void shutdownAll() {
		Place[] places = x10.lang.Runtime.places();
		for (int i=places.length-1;i>=0;i--) {
			if (places[i] instanceof LocalPlace_c) {
				((LocalPlace_c) places[i]).shutdown();
			}
		}
	}
	
	
	public  void runArrayConstructor(FatPointer owningObject,int elementType,
			int elSize,dist d,IntArray.pointwiseOp op, boolean safe,boolean mutable){
		
		x10.lang.place owningPlace = here();
		
		//Find/create corresponding fatpointer at current place (this)
		FatPointer localHandle = findGlobalObject(owningObject);
		if(localHandle != null){
			throw new RuntimeException("Multiple array constructor calls for "+owningObject+" at place "+this);
		}
		
		localHandle = registerGlobalObject(owningObject);
		System.out.println("in run array constructor... owner:"+owningObject);
			System.out.println("creating:"+localHandle);
		java.lang.Object arrayObject=null;
		region myRegion = d.restrictToRegion(x10.lang.Runtime.here());
		int arraySize = myRegion.size();
		
		switch(elementType){
		case ElementType.INT:
			x10.array.distributed.IntArray_c localArray= new x10.array.distributed.IntArray_c (d, safe, mutable,false,arraySize);
		    arrayObject = (java.lang.Object)localArray;
		    localArray.localPointwise(myRegion,localArray,op);
		    
		break;
		default:
			throw new RuntimeException("need to support alloc of "+elementType);
		}
		localHandle.setObject(arrayObject);
		
		System.out.println("completed init size:"+arraySize+" new handle:"+arrayObject);
	}

		
		public  void runArrayConstructor(FatPointer owningObject,int elementType,
				int elementSize,dist d,long initValue,boolean safe,boolean mutable){
			x10.lang.place owningPlace = here();
			
			//Find/create corresponding fatpointer at current place (this)
			FatPointer localHandle = findGlobalObject(owningObject);
			if(localHandle != null){
				throw new RuntimeException("Multiple array constructor calls for "+owningObject+" at place "+this);
			}
			
			localHandle = registerGlobalObject(owningObject);
			System.out.println("in run array constructor C ... owner:"+owningObject);
				System.out.println("creating:"+localHandle);
			java.lang.Object arrayObject=null;
			
			region myRegion = d.restrictToRegion(x10.lang.Runtime.here());
			int arraySize = myRegion.size();
			
			switch(elementType){
			case ElementType.INT:
				x10.array.distributed.IntArray_c localArray= new x10.array.distributed.IntArray_c (d, safe, mutable, false, arraySize);
			arrayObject = (java.lang.Object)localArray;
			   localArray.scan(myRegion,localArray,new IntArray.Assign((int)initValue));
			
			break;
			default:
				throw new RuntimeException("need to support alloc of "+elementType);
			}
			localHandle.setObject(arrayObject);
			
			System.out.println("completed init size:"+arraySize+" new handle:"+arrayObject);
		}
		public void remoteScan(FatPointer dest,FatPointer src,AsyncResult syncPoint,x10.lang.intArray.binaryOp op){
			
			throw new RuntimeException("unimplemented");//TODO: implement
		}
		
		public int remoteReadInt(FatPointer fp,point p){
			return 0;
		}
		public void remoteReductionInt(AsyncResult syncPoint,int unit){
			throw new RuntimeException("unimplemented");//TODO: implement
		}
		public void remoteWriteInt(FatPointer fp,point p,int val){
			throw new RuntimeException("unimplemented");//TODO: implement
		}
		public void remoteCopy(FatPointer dest,FatPointer src,AsyncResult syncPoint){
			//copy all points from src to dest--make sure to check the distribution.  This is called
			//from restriction, so  dest can have a dist which is a subset of src
			throw new RuntimeException("unimplemented");//TODO: implement
		}
		
		
		final public FatPointer registerGlobalObject(java.lang.Object o){
			java.lang.Object searchObject;
			int owningVM;
			int theKey=0;
			if(o instanceof FatPointer) {
				searchObject=((FatPointer)o).getObject();
				owningVM = ((FatPointer)o).getOwningVM();
				theKey = ((FatPointer)o).getKey();
			}
			else {
				searchObject = o;
				owningVM = FatPointer.UNASSIGNED;
				theKey = o.hashCode();
			}
			
			//    	TODO check if o is a global object
			if(_fatPointerMap.contains(searchObject)){
				return (FatPointer)_fatPointerMap.get(searchObject);
			}
			FatPointer newEntry = new FatPointer(o);
			newEntry.setOwningVM(owningVM);
			newEntry.setKey(theKey);
			_fatPointerMap.put(o,newEntry);
			return newEntry;
		}
		
		final public FatPointer shadowRemoteEntry(java.lang.Object o,int key){
			java.lang.Object searchObject;
			int owningVM;
			if(o instanceof FatPointer) {
				searchObject=((FatPointer)o).getObject();
				owningVM = ((FatPointer)o).getOwningVM();
			}
			else {
				searchObject = o;
				owningVM = FatPointer.UNASSIGNED;
			}
			
			if(_fatPointerMap.contains(searchObject)){
				throw new RuntimeException("Object already entred in Global map:"+o);
			}
			int vmId=0;
			FatPointer newEntry = new FatPointer(searchObject);
			newEntry.setKey(key);
			newEntry.setOwningVM(owningVM);
			_fatPointerMap.put(searchObject,newEntry);
			return newEntry;
		}
		final public FatPointer findGlobalObject(java.lang.Object o){
			java.lang.Object searchObject;
			int owningVM;
			if(o instanceof FatPointer) {
				searchObject=((FatPointer)o).getObject();
				owningVM = ((FatPointer)o).getOwningVM();
			}
			else {
				searchObject = o;
				owningVM = FatPointer.UNASSIGNED;
			}
			if(_fatPointerMap.contains(searchObject)){
				return (FatPointer)_fatPointerMap.get(searchObject);
			}
			
			return null;
		}
		
		
		/**
		 * Copy src to dest array over the region at this particular place
		 */
		public void remoteSectionCopy(FatPointer dest,FatPointer src){
			x10Array srcArray = (x10Array)(src.getObject());
			x10Array destArray = (x10Array)(dest.getObject());
			region localRegion = srcArray.getDistribution().restrictToRegion(here());
			destArray.copyLocalSection(destArray,srcArray,localRegion);
		}
		
		/**
		 * Intersect dest and source, and if a point in source is not in the dest region,
		 * copy it from source
		 * @param dest
		 * @param source
		 */
		public void remoteUnion(FatPointer dest,FatPointer src){
			x10Array destArray = (x10Array)(dest.getObject());
			x10Array srcArray = (x10Array)(dest.getObject());
			region localRegion = destArray.getDistribution().restrictToRegion(here());
			
			destArray.copyDisjoint(destArray,srcArray,localRegion);
			
			
		}
} // end of LocalPlace_c

