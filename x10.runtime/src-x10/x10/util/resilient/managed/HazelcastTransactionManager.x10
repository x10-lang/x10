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

package x10.util.resilient.managed;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.interop.Java;
import x10.io.Console;
import x10.io.Unserializable;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.resilient.ResilientTransactionalMap;
import x10.util.resilient.ResilientTransactionManager;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The HazelcastMap class implements a Resilient Transaction Manager using Hazelcast as the underlying implementation.
 */

public class HazelcastTransactionManager
		 extends ResilientTransactionManager
			 implements Unserializable {

	public static def runHazelcastTransaction[T](run:(ResilientTransactionManager)=>T):T {
        	val hz = getHazelcastInstance();
			try {
				return hz.executeTransaction(new com.hazelcast.transaction.TransactionalTask() {
						public def execute(context:com.hazelcast.transaction.TransactionalTaskContext)
							throws com.hazelcast.transaction.TransactionException : Any {
							finish {
								return run(new HazelcastTransactionManager(context));
							}
						}}) as T;
		} catch(e:com.hazelcast.transaction.TransactionException) {
			throw new Exception(e);
		}
    }

    public static def getMap[K,V](manager:ResilientTransactionManager, mapName:String) {V haszero} : ResilientTransactionalMap[K,V] {
		if(! (manager instanceof HazelcastTransactionManager)) {
			throw new Exception("HazelcastTransactionManager.getMap(" + manager + ", " + mapName + "): Non Hazelcast manager is not supported in this configuration");
		}
		val man = manager as HazelcastTransactionManager;
		return man.getMap[K,V](mapName);
	}

    private def getMap[K,V](mapName:String) {V haszero} : HazelcastTransactionalMap[K,V] {
		val baseMap:com.hazelcast.core.TransactionalMap
								= context.getMap(mapName);
		if (baseMap == null) {
			throw new Exception("HazelcastTransactionalMap.this("+mapName+"): HazelcastTransactionalMap is not supported in this configuration");
		}
		return new HazelcastTransactionalMap[K,V](baseMap);
		
	}

    protected val context: com.hazelcast.transaction.TransactionalTaskContext;

    /**
     * Constructor method.  This is called by factory method getMap
     * and is not directly invoked by applications.
     */
    def this(context:com.hazelcast.transaction.TransactionalTaskContext) {
        this.context = context;
    }

	/**
	 * Helper method used to get the underlying hazelcast instance
	 */
	@Native("java", "x10.x10rt.X10RT.getHazelcastInstance()")
	private native static def getHazelcastInstance(): com.hazelcast.core.HazelcastInstance;
}
