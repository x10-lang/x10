/**
 An X10 implementation of the Garg and Sabharwal asynchronous software routing algorithm for RandomAccess.
 
 It assumes that only one thread will be spawned per place, and this thread will return to polling on its input
 whenever it has nothing to do (i.e. no active async to execute), unless it is the place 0 thread, 
 in which case the thread will signal termiantion to all other threads. The way the program is implemented below
 this could cause termination even when there are non-empty buffers at other places. However, the total size
 of these buffers is bounded by 1024, and hence the maximum number of unprocessed updates left behind will be
 1024*p. If we chose N so that 1024*p < 0.01*N, we will meet the requirements of the program (that at most 1% of 
  the updates can be ignored).
  
  @author vj 9/18/2007
*/
public class RandomAccessAsync {
	public final static int IMax=10, JMax=10, KMax=10;
	public final static int buffSize = 1024/(IMax + JMax + KMax);
	public final static int[] dimMax = new int[] {IMax, JMax, KMax};
	public final static region(:rank==1) Dims = [1:3];
	private final static dist UNIQUE=dist.UNIQUE;
	public final static region(:rank==1)[.] regions = (region(:rank==1)[.]) new region[Dims](point [i]) { return [1:dimMax[i]];};
	final static Buckets[.] buckets  = new Buckets[UNIQUE] (point p) { 
		return new Buckets();};
	
	static class Buckets {
		DimBuckets[/*:region==Dims*/.] dimBuckets = new DimBuckets[Dims](point [i]) { return new DimBuckets(i);};
	}
	static class DimBuckets(int dim) {
		nullable<Bucket> fullBucket;
		
		Bucket[/*:region==regions[dim]*/.] bucket;
		DimBuckets(int/*(Dims)*/dim) {
			property(dim);
			bucket = new Bucket[regions[dim]](point [i]) {
				return new Bucket(DimBuckets.this, i);
			}
		}
	}
	
	static class Bucket {
		final long[] buffer;
		final DimBuckets bucketRow;
		final int index; // its position in the row of buckets for its dimension.
		int count;
		
		Bucket(DimBuckets bucketRow, int i) {
			this.bucketRow = bucketRow;
			buffer = new long[buffSize];
			index =i;
			count=0;
		}
		place target() {
			return targetPlace(bucketRow.dim, index);
		}
		long value [.] copy() {
			return new long value [[0:buffSize-1]](point [i]) { return buffer[i];};
		}
	}
	static boolean noBucketFull() {
		Buckets b = buckets[here.id];
		return b.dimBuckets[1].fullBucket == null 
		&& b.dimBuckets[2].fullBucket==null && b.dimBuckets[3].fullBucket==null;
	}
	public static int nextDim(int dim) {
		return dim==1?2:(dim==2)?3:0;
	}
	
	public final static long NUM_UPDATES = 1; // Tong to fix!
	public static place targetPlace(int dim, int i) { return here;} // Tong to fix.
	public static int nextHop(long datum, int dim) { return 0;}// Tong to fix.
	public static boolean isLocal(long datum){ return false;}// Tong to fix.
	public static void update(long datum) { } // Tong to fix.
	public static long nextUpdate() { return 0L;} // Tong to fix.
	
	public static void routeUpdates(long datum) {
		final int nextDim = 1;
		if (isLocal(datum))
			update(datum);
		else {
			DimBuckets row = buckets[here.id].dimBuckets[nextDim];
			pushIntoBucket(datum, nextDim, row);
		}
	}
	public static void pushIntoBucket(long datum, int dim, DimBuckets row) {
		int nextHop = nextHop(datum, dim);
		Bucket d = row.bucket[nextHop];
		d.buffer[d.count++]=datum;
		if (d.count == buffSize-1) 
			row.fullBucket=d;
	}
	public static void routeUpdates(final Bucket b) {
		int nextDim = nextDim(b.bucketRow.dim);
		DimBuckets row = buckets[here.id].dimBuckets[nextDim];
		for (; b.count < buffSize; b.count++) {
			long datum = b.buffer[b.count];
			if (isLocal(datum)) {
				update(datum);
			} else {
				assert nextDim > 0;
				if (row.fullBucket != null) return;
				pushIntoBucket(datum, nextDim, row);
				
			}
		}
	}
	/**
	  Check if any buffer is full. If so, send it to its destination through an async, which, 
	  on arriving at the destination will wait until the receiveBuffer at the destination for
	  this dimension is empty, and then will copy this buffer in, and routeUpdates from it.
	*/
	public static void checkFull() {
		Buckets bs = buckets[here.id];
		for (point [dim] : Dims) {
			DimBuckets d = bs.dimBuckets[dim];
			final nullable<Bucket> b = d.fullBucket;
			if (b !=null /* && canSend(dim) */) {
				// I currently dont see a way of checking whether it is 
				// possible to send. This needs to be thought through
				// we dont want to spawn the subsequent async, i.e. send a message
				// unless the previous message sent from here has been received.
				// This is easy enough to program explicitly -- essentially we 
				// send one message at a time from one place to another, and 
				// use a completion counter to check that the message has been received
				// before sending another. So this is to be implemented still.
				
				// As things stand, these asyncs can be spawned eagerly and will
				// correspond to messages being sent into the network eagerly.
				d.fullBucket=null;
				Bucket bt = (Bucket) b;
				final long value[.] data = bt.copy();
//				 The data has been copied to be sent, so the count can be set to 0.
				b.count = 0;
				
				async (bt.target()) {
					// Note that there is no call to yield in the body of this activity.
					// Hence when this activity is executed it will run to completion.
					final Bucket recvBuffer = buckets[here.id].dimBuckets[dim].bucket[here.id];
					
					// This async is necessary only because of the following when.
					// This will be triggered when routeUpdates(recvBuffer) causes
					// recVbuffer.count to go to zero.
					async {
					    when(recvBuffer.count==0) { 
						for (int i=0; i < buffSize; i++) recvBuffer.buffer[i]=data[i];
						  routeUpdates(recvBuffer);
					    }
					    checkFull();
					}
				}
			}
		}
	}
	/**
	 To be supplied by the X10 runtime. Is an indication to the thread that is should consider 
	 yielding the current activity, and running some other activity. 
	*/
	public static void yield() {} 
	/**
	  Top-level loop. We assume that the program is started with one thread assigned to each place.
	  This thread will run each activity to completion. An activity may use yield() to permit the thread
	  to execute other activities.
	*/
	public static void loop() {
		finish ateach(point [p] : UNIQUE )  {
				for (long n=1; n <= NUM_UPDATES; n++) {
					checkFull();
					// This call to poll permits the thread executing this async
					// to check for incoming asyncs and execute them.
					yield();
					if (noBucketFull()) {
						final long x = nextUpdate();
						routeUpdates(x);
					}
				}
		}
	}
}
