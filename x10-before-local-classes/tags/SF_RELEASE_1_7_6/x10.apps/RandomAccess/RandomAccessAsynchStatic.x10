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
  modified by tw 10/4/2007:
	  filled in the missing logic to Vijay's skeleton;
          changed couple variable names to increase readability;
          corrected serveral correction bugs.
*/
public class RandomAccessAsynchStatic {
	final static long POLY = 0x0000000000000007;
	final static long PERIOD = 1317624576693539401L;
	public final static int IMax=3, JMax=3, KMax=3;
	public final static int buffSize = 1024/(IMax + JMax + KMax);
	public final static int[] dimMax = new int[] {IMax, JMax, KMax};
	public final static region(:rank==1) DIMS = [1:3];
	final static dist UNIQUE=dist.UNIQUE;
	final static int NUM_PLACES = place.MAX_PLACES;//should be equal to IMax*JMax*KMax.
	final static int PLACE_ID_MASK = NUM_PLACES-1;
	final static int point[.] placeIJK = new point [[0:NUM_PLACES-1]] (point [i]){ 
		int planeSize = IMax * JMax;
		return [i/planeSize, (i%/planeSize)/IMax, (i%planeSize)%IMax];};
	public final static region(:rank==1)[.] buffRegions = (region(:rank==1)[.]) new region[DIMS](point [i]) { return [0:dimMax[i]-1];};
	public final static int logLocalTableSize = 10; // Tong to fix!
	public final static int localTableSize = 1 << logLocalTableSize;
	public final static int TableSize = NUM_PLACES * localTableSize;
	public final static long Mask =  TableSize-1;
	public final static int numLocalUpdates = 4 * localTableSize;
	
	final static region(:zeroBased&&rank==1&&rect) R = [0:TableSize-1]; //can't handle long at this moment
	final static dist(:zeroBased&&rank==1&&rect) DD = (dist(:zeroBased&&rank==1&&rect))dist.factory.block(R);
	final static long[:self.rect && self.zeroBased && self.rank==1] Table = new long[DD] (point [i]) {return i;};
	
	final static localBuckets[.] DistBuckets  = new localBuckets[UNIQUE] (point p) { 
		return new localBuckets();};
	
	final static localBuckets[.] DistReceivers  = new localBuckets[UNIQUE] (point p) { 
		return new localBuckets(1);};	
	
	static class localBuckets {
		localBuckets(){
			DimBuckets[/*:region==DIMS*/.] dimBuckets = new DimBuckets[DIMS](point [i]) {
				return new DimBuckets(i);};
		}
		
		localBuckets(int size){
			DimBuckets[/*:region==DIMS*/.] dimBuckets = new DimBuckets[DIMS](point [i]) {
				return new DimBuckets(i,1);};
		}
	}
	
	static class DimBuckets(int dim) {
		nullable<Bucket> fullBucket;
		
		Bucket[/*:region==buffRegions[dim]*/.] bucket;
		DimBuckets(int/*(DIMS)*/dim) {
			property(dim);
			bucket = new Bucket[buffRegions[dim]](point [i]) {
				return new Bucket(DimBuckets.this, i);
			}
		}
		DimBuckets(int/*(DIMS)*/dim, int size) {
			property(dim);
			bucket = new Bucket[[1:size]](point [i]) {
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
			return targetPlace(bucketRow.dim, index, int here.id);
		}
		long value [.] copy() {
			return new long value [[0:buffSize-1]](point [i]) { return buffer[i];};
		}
	}
	static boolean noBucketFull() {
		localBuckets b = DistBuckets[here.id];
		return b.dimBuckets[1].fullBucket == null 
		&& b.dimBuckets[2].fullBucket==null && b.dimBuckets[3].fullBucket==null;
	}
	public static int nextDim(int dim) {
		return dim==1?2:(dim==2)?3:3;
	}
	
	  /* Utility routine to start random number generator at Nth step */
	    static long HPCC_starts(long n) {
	        int i, j;
	        long[] m2 = new long[64];
	        long temp, ran;
	
	        while (n < 0) n += PERIOD;
	        while (n > PERIOD) n -= PERIOD;
	        if (n == 0) return 0x1;
	
	        temp = 0x1;
	        for (i=0; i<64; i++) {
	            m2[i] = temp;
	            temp = (temp << 1) ^ ((long) temp < 0 ? POLY : 0);
	            temp = (temp << 1) ^ ((long) temp < 0 ? POLY : 0);
	        }
	    
	        for (i=62; i>=0; i--)
	            if (((n >> i) & 1) != 0)
	                break;
	    
	        ran = 0x2;
	        while (i > 0) {
	            temp = 0;
	            for (j=0; j<64; j++)
	                if (((ran >> j) & 1) != 0)
	                    temp ^= m2[j];
	            ran = temp;
	            i -= 1;
	            if (((n >> i) & 1) != 0)
	                ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
	        }
	        return ran;
	    }
	public static place targetPlace(int dim, int i, int placeID) { 
		point placeP = placeIJK[placeID];
		int [.] ijk = new int [DIMS] (point [idx]) { return placeP[idx];}
		ijk[dim] = i;
		int planeSize = IMax*JMax;
		return UNIQUE[ijk[3]*planeSize+ijk[2]*IMax+ijk[1]];
	} // Tong to fix.
	public static int nextHop(long datum, int dim) { 
		int placeID = (int)((datum>>LogTableSize) & PLACE_ID_MASK);
		point ijk = placeIJK[placeID];
		return ijk[dim];
	}// Tong to fix.
	public static boolean isLocal(long datum){ 
		return here.id == (int)((datum>>LogTableSize) & PLACE_ID_MASK);
	}// Tong to fix.
	public static void update(long datum) { 
		final long temp=datum;
		final int index = (int)(temp & Mask);
		Table[index] ^= temp; //must be local	
	} // Tong to fix.
	public static long nextUpdate(long ran) { return (ran << 1)^((long) ran < 0 ? POLY : 0);} // Tong to fix.
	
	public static void routeUpdates(long datum) {
		final int nextDim = 1;
		if (isLocal(datum))
			update(datum);
		else {
			DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
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
		if (nextDim ==3){
			DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
			for (; b.count < buffSize; b.count++) {
				long datum = b.buffer[b.count];
				update(datum);
			}
		}
		else{
			DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
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
	}
	/**
	  Check if the processing at the destination has completed.  This means that
	  the destination is ready to receive some more data.
	*/
	public static boolean canSend(Bucket b) {
		return b.count == 0;
	}
	/**
	  Check if any buffer is full. If so, send it to its destination through an async, which, 
	  on arriving at the destination will wait until the receiveBuffer at the destination for
	  this dimension is empty, and then will copy this buffer in, and routeUpdates from it.
	*/
	public static void checkFull() {
		localBuckets bs = DistBuckets[here.id];
		for (point [dim] : DIMS) {
			DimBuckets d = bs.dimBuckets[dim];
			final nullable<Bucket> b = d.fullBucket;
			//if (b != null /* && canSend(dim) */) { // }
				// I currently dont see a way of checking whether it is 
				// possible to send. This needs to be thought through
				// we dont want to spawn the subsequent async, i.e. send a message
				// unless the previous message sent from here has been received.
				// This is easy enough to program explicitly -- essentially we 
				// send one message at a time from one place to another, and 
				// use a completion counter to check that the message has been received
				// before sending another. So this is to be implemented still.
			if (b != null && canSend(b)) {
				// [IP] The finish below ensures that we do not set the count
				// to 0 until the remote processing has completed.

				// As things stand, these asyncs can be spawned eagerly and will
				// correspond to messages being sent into the network eagerly.
				d.fullBucket=null;
				Bucket bt = (Bucket) b;
				final long value[.] data = bt.copy();

				async {
					// Yogish says that this finish fixes the deadlock problem,
					// but will limit the throughput.
					// The situation is the following: the current process is
					// ready to send, and the remote side is ready to receive
					// (i.e., the async has terminated), but the finish
					// machinery is still waiting for the update to the
					// completion counter, which is being delayed in the
					// network.
					// On BlueGene they did not have to resort to a finish
					// because they could receive independently along various
					// dimensions.  As far as he knows, LAPI does not provide
					// such functionality.
					// In the original code, the deadlock would have happened
					// when two nodes send each other messages along the same
					// dimension, and both have their buffers full.  The
					// messages along other dimensions will end up queued
					// behind the blocked messages.
					// More details on the deadlock situation can be found in
					// a technical report cited in their paper.
					// [IP] We might be able to avoid the throughput problem
					// by having a specialized clever finish implementation.
					// Let's discuss...
					finish async (bt.target()) {
						// Note that there is no call to yield in the body of this activity.
						// Hence when this activity is executed it will run to completion.
						final Bucket recvBuffer = DistBuckets[here.id].dimBuckets[dim].bucket[here.id];

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
					// The data has been copied to be sent, so the count can be set to 0.
					b.count = 0;
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
	public static void main(String[] args) {
		finish ateach(point [p] : UNIQUE )  {
			long ran = HPCC_starts(p*numLocalUpdates);
			for (long n=1; n <= numLocalUpdates; n++) {
				checkFull();
				// This call to poll permits the thread executing this async
				// to check for incoming asyncs and execute them.
				yield();
				if (noBucketFull()) {
					routeUpdates(ran);
					ran = (ran << 1)^((long) ran < 0 ? POLY : 0);
				}
			}
		}
	}
}
