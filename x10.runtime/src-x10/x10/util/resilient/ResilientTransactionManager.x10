/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.resilient;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The ResilientTransactionManager enables running a transaction with multiple maps
 * (if this is supported by the resilient map)
 * There is currently a Hazelcast implementation of a resilient store which
 * implements this interface for managed X10.  Native implementations are also
 * possible (but not yet implemented).
 */
public abstract class ResilientTransactionManager {

	@Native("java", "x10.util.resilient.managed.HazelcastTransactionManager.runHazelcastTransaction__0$1x10$util$resilient$ResilientTransactionManager$3x10$util$resilient$managed$HazelcastTransactionManager$$T$2$G(#T$rtt, #run)")
	public static def runTransaction[T](run:(ResilientTransactionManager)=>T):T {
        throw new Exception("Running transactions with a ResilientTransactionManager is not supported in this configuration");
    }
	
	/**
	   Returns a ResilientTransactionalMap[K,V].
	   This should just be an instance method with type
	   getMap[K,V](mapName:String):ResilientTransactionalMap[K,V]
	   but this is not well supported in X10.
	 */
     @Native("java", "x10.util.resilient.managed.HazelcastTransactionManager.getMap(#K$rtt, #V$rtt, #manager, #mapName)")
	 public static def getMap[K,V](manager:ResilientTransactionManager, mapName:String) {V haszero} : ResilientTransactionalMap[K,V] {
		 throw new Exception("Running transactions with a ResilientTransactionManager is not supported in this configuration");
	}
}
