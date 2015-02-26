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
 * The ResilientTransactionalMap abstract class defines an interface for transactional operations on resilient stores.
 * There is currently a Hazelcast implementation of a resilient store which
 * implements this interface for managed X10.  Native implementations are also
 * possible (but not yet implemented).

 * An instance of ResilientTransactionalMap is obtained either by calling {@link ResilientTransactionalMap#runTransaction}
 * or by calling {@link ResilientTransactionManager#getMap} inside a transaction started with {@link ResilientTransactionManager#runTransaction}.
 * @see ResilientTransactionManager
 */
public abstract class ResilientTransactionalMap[K,V] {V haszero} implements ResilientBaseMap[K,V] {

	/**
     * Factory method to create resilient map.
     */
	public static def runTransaction[K,V,T](mapName:String, run:(ResilientTransactionalMap[K,V])=>T){V haszero}:T {
		return ResilientTransactionManager.runTransaction[T]
			((manager:ResilientTransactionManager)=>run(ResilientTransactionManager.getMap[K,V](manager, mapName)));
    }

	/**
     * Shorthand for {@link #get}
     */
    public operator this(k:K):V
		= get(k);

	 /**
     * Shorthand for {@link #put}
     */
    public operator this(k:K)=(v:V):V
		= put(k,v);
	
	 /**
     * Get the value of key k in the resilient map. 
	 * Also ``hints'' to the implementation that we are likely to
	 * update the key later in the transaction.
     */
    public def getForUpdate(k:K):V = get(k);
}
