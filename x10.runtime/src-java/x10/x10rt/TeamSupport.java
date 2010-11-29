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

	
	private static native void nativeMakeImpl(int[] places, int count, int[] result, FinishState fs);
	
	static synchronized native void initializeTeamSupport();
	
}
