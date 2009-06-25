/**

 An X10 implementation of the Garg and Sabharwal asynchronous software
 routing algorithm for RandomAccess.
 
 It assumes that only one thread will be spawned per place, and this
 thread will return to polling on its input whenever it has nothing to
 do (i.e. no active async to execute), unless it is the place 0
 thread, in which case the thread will signal termiantion to all other
 threads. The way the program is implemented below this could cause
 termination even when there are non-empty buffers at other
 places. However, the total size of these buffers is bounded by 1024,
 and hence the maximum number of unprocessed updates left behind will
 be 1024*p. If we chose N so that 1024*p < 0.01*N, we will meet the
 requirements of the program (that at most 1% of the updates can be
 ignored).
  
  @author vj 9/18/2007
  modified by tw 10/4/2007:
    filled in the missing logic to Vijay's skeleton;
          changed couple variable names to increase readability;
          corrected serveral correction bugs.
*/
public class RandomAccessAsyncStaticWithPools {
  const long POLY = 0x0000000000000007L;
  const long PERIOD = 1317624576693539401L;
  const int IMax=8, JMax=8, KMax=8, planeSize=IMax*JMax;
  const int X_DIM=0, Y_DIM=1, Z_DIM=2;
  const int buffSize = 1024/(IMax + JMax + KMax);
  const int[] dimMax = new int[] {IMax, JMax, KMax};
  const region(:rank==1) DIMS = [X_DIM:Z_DIM];
  const dist UNIQUE=dist.UNIQUE;
  const int NUM_PLACES = place.MAX_PLACES;
  static {
	  // TODO: We should modify the program so that we dont need the following assumption.
	  assert IMax == JMax && JMax == KMax; 
  
	  assert NUM_PLACES == IMax*JMax*KMax;
	  assert pow2(NUM_PLACES);
  }
  public static boolean pow2(int N) {
	  if ( N <= 1) return N==1;
	  while (N > 1) {
		  if (N % 2 !=0) return false;
		  N=N >> 1;
	  }
	  return true;
	  
  }
  const int PLACE_ID_MASK = NUM_PLACES-1;
  const point[.] placeIJK = new point [[0:NUM_PLACES-1]] (point [i]){ 
    return [i/planeSize, (i%planeSize)/IMax, (i%planeSize)%IMax];
    };
  const region(:rank==1)[.] buffRegions = (region(:rank==1)[.]) new region[DIMS](point [i]) { 
    return [0:dimMax[i]-1];
    };
  const int logLocalTableSize = 10; 
  // TODO: Check that this is correct.
  const int localTableSize = 1 << logLocalTableSize;
  const int TableSize = NUM_PLACES * localTableSize;
  const long Mask =  TableSize-1;
  const int numLocalUpdates = 4 * localTableSize;
  
    //can't handle long at this moment
    const region(:zeroBased&&rank==1&&rect) R = [0:TableSize-1]; 
    const dist(:zeroBased&&rank==1&&rect) DD = dist.factory.block(R);
    const  long[:self.rect && self.zeroBased && self.rank==1] 
      Table = new long[DD] (point [i]) {return i;};
  
  final static localBuckets[.] DistBuckets  = new localBuckets[UNIQUE] (point p) { 
    return new localBuckets();};
  
  static class localBuckets {
    DimBuckets[:region==DIMS] dimBuckets;
    localBuckets(){
       dimBuckets = (DimBuckets[:region==DIMS]) new DimBuckets[DIMS](point [i]) {
        return new DimBuckets(i);};
    }
  }
  
  static class DimBuckets(int dim) {
    nullable<Bucket> fullBucket;
    
    Bucket[/*:region==buffRegions[dim]*/.] bucket;
    DimBuckets(int/*(DIMS)*/dim) {
      property(dim);
      bucket = new Bucket[buffRegions[dim]](point [i]) {
        return new Bucket(DimBuckets.this, i);
      };
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
      return targetPlace(bucketRow.dim, index, here.id);
    }
    long value [.] copy() {
      return new long value [[0:buffSize-1]](point [i]) { return buffer[i];};
    }
  }
  static boolean noBucketFull() {
    final localBuckets b = DistBuckets[here.id];
    return b.dimBuckets[X_DIM].fullBucket == null 
           && b.dimBuckets[Y_DIM].fullBucket==null 
           && b.dimBuckets[Z_DIM].fullBucket==null;
  }
  public static int nextDim(int dim) {
    return dim==X_DIM?Y_DIM:(dim==Y_DIM)?Z_DIM:Z_DIM;
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
    final point placeP = placeIJK[placeID];
    int [.] ijk = new int [DIMS] (point [idx]) { return placeP[idx];};
    ijk[dim] = i;
    int planeSize = IMax*JMax;
    return UNIQUE[ijk[3]*planeSize+ijk[2]*IMax+ijk[1]];
  } 
  public static int nextHop(long datum, int dim) { 
    int placeID = (int)((datum>>logLocalTableSize) & PLACE_ID_MASK);
    point ijk = placeIJK[placeID];
    return ijk[dim];
  }
  public static boolean isLocal(long datum){ 
    return here.id == (int)((datum>>logLocalTableSize) & PLACE_ID_MASK);
  }
  public static void update(final long datum) { 
    assert isLocal(datum);
    final int index = (int)(datum & Mask);
    Table[index] ^= datum;
  } 
  public static long nextUpdate(long ran) { return (ran << 1)^((long) ran < 0 ? POLY : 0);} 
  
  public static void routeUpdates(long datum) {
    final int nextDim = X_DIM;
    if (isLocal(datum))
      update(datum);
    else {
      DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
      pushIntoBucket(datum, nextDim, row);
    }
  }
  /**
  Push this datum into the appropriate bucket in the given row. (The row
  corresponds to the given dimension.) Note that this may cause the d
  datum to be added to the buffer for the current node. 
  */
  public static void pushIntoBucket(long datum, int dim, DimBuckets row) {
    int nextHop = nextHop(datum, dim);
    Bucket d = row.bucket[nextHop];
    d.buffer[d.count++]=datum;
    if (d.count == buffSize-1) {
      assert row.fullBucket==null;
      row.fullBucket=d;
    }
  }
  public static void routeUpdates(final Bucket b) {
    final int nextDim = nextDim(b.bucketRow.dim);
    if (nextDim ==Z_DIM){ // now these are all local updates.
      DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
      for (; b.count >= 0; b.count--) {
        long datum = b.buffer[b.count];
        update(datum);
      }
      return;
    } 
    final DimBuckets row = DistBuckets[here.id].dimBuckets[nextDim];
    for (; b.count >= 0; b.count--) {
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
    localBuckets bs = DistBuckets[here.id];
    for (point [dim] : DIMS) {
      final DimBuckets d = bs.dimBuckets[dim];
      final nullable<Bucket> b = d.fullBucket;
      if (b != null /*&& canSend(b)*/) {
        d.fullBucket=null;
        Bucket bt = (Bucket) b;
        final long value[.] data = bt.copy();
        async ( bt.target() ) {
          final Bucket recvBuffer = DistBuckets[here.id].dimBuckets[dim].bucket[here.id];
            when(recvBuffer.count==0) 
		for (int i=0; i < buffSize; i++) recvBuffer.buffer[i]=data[i];
            do  {
            	routeUpdates(recvBuffer);
            	checkFull();
            	yield();
            } while (recvBuffer.count > 0);
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
    This thread will run each activity to completion. An activity may use yield() to permit the 
    thread to execute other activities.
  */
  public static void main(String[] args) {
    finish ateach(point [p] : UNIQUE )  {
      long ran = HPCC_starts(p*numLocalUpdates);
      for (long n=1; n <= numLocalUpdates; n++) {
        checkFull();
        yield();
        if (noBucketFull()) {
          routeUpdates(ran);
          ran = (ran << 1)^((long) ran < 0 ? POLY : 0);
        }
      }
    }
  }
}
