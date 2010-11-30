/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.x10rt;

import x10.core.IndexedMemoryChunk;
import x10.lang.FinishState;
import x10.lang.Place;

public class TeamSupport {

	private static FinishState activityCreationBookkeeping() {
		FinishState fs = x10.lang.Runtime.activity().finishState();
		fs.notifySubActivitySpawn(x10.lang.Runtime.here());
		fs.notifyActivityCreation();
		return fs;
	}

	// Invoked from native code.
	private static void activityTerminationBookkeeping(FinishState fs) {
		fs.notifyActivityTermination();
	}
	
	public static void nativeMake(IndexedMemoryChunk<Place> places, int count, IndexedMemoryChunk<Integer> result) {
	    Place[] np = (Place[])places.getBackingArray();
	    int[] int_places = new int[np.length];
	    for (int i=0; i<places.length; i++) {
	        int_places[i] = np[i].id;
	    }
	    int[] nr = result.getIntArray();

	    FinishState fs = activityCreationBookkeeping();

		nativeMakeImpl(int_places, count, nr, fs);
	}
	
	
	public static int nativeSize(int id) {
	    return nativeSizeImpl(id);
	}
	
	public static void nativeBarrier(int id, int role) {
	    FinishState fs = activityCreationBookkeeping();
	    
	    nativeBarrierImpl(id, role, fs);
	}
	
    public static void nativeScatter(int id, int role, int root, IndexedMemoryChunk<?> src, int src_off, 
	                                 IndexedMemoryChunk<?> dst, int dst_off, int count) {
        System.err.println("About to die in nativeScatter");
		throw new UnsupportedOperationException("nativeScatter");
	}
	
    public static void nativeBcast(int id, int role, int root, IndexedMemoryChunk<?> src, int src_off, 
                                     IndexedMemoryChunk<?> dst, int dst_off, int count) {
        System.err.println("About to die in nativeBcast");
        throw new UnsupportedOperationException("nativeBcast");
    }
    
    public static void nativeAllToAll(int id, int role, IndexedMemoryChunk<?> src, int src_off, 
                                      IndexedMemoryChunk<?> dst, int dst_off, int count) {
        System.err.println("About to die in nativeAllToAll");
        throw new UnsupportedOperationException("nativeAllToAll");
    }
    
    public static void nativeAllReduce(int id, int role, IndexedMemoryChunk<?> src, int src_off, 
                                       IndexedMemoryChunk<?> dst, int dst_off, int count, int op) {
        System.err.println("About to die in nativeAllReduce");
        throw new UnsupportedOperationException("nativeAllReduce");
    }

    public static void nativeAllReduce(int id, int role, IndexedMemoryChunk<?> src,
                                       IndexedMemoryChunk<?> dst, int op) {
        System.err.println("About to die in nativeAllReduce");
        throw new UnsupportedOperationException("nativeAllReduce");
    }

    public static void nativeIndexOfMax(int id, int role, IndexedMemoryChunk<?> src,
                                        IndexedMemoryChunk<?> dst) {
        System.err.println("About to die in nativeIndexOfMax");
        throw new UnsupportedOperationException("nativeIndexOfMax");
    }

    public static void nativeIndexOfMin(int id, int role, IndexedMemoryChunk<?> src,
                                        IndexedMemoryChunk<?> dst) {
        System.err.println("About to die in nativeIndexOfMin");
        throw new UnsupportedOperationException("nativeIndexOfMin");
    }

    public static void nativeSplit(int id, int role, int color, int new_role, IndexedMemoryChunk<Integer> result) {
        System.err.println("About to die in nativeSplit");
       throw new UnsupportedOperationException("nativeSplit");
    }
    
    public static void nativeDel(int id, int role) {
        FinishState fs = activityCreationBookkeeping();
        
        nativeDelImpl(id, role, fs);
    }
    
	private static native void nativeMakeImpl(int[] places, int count, int[] result, FinishState fs);
	
	private static native int nativeSizeImpl(int id);
	
	private static native void nativeBarrierImpl(int id, int role, FinishState fs);

	private static native void nativeDelImpl(int id, int role, FinishState fs);
	
	static synchronized native void initializeTeamSupport();
}
