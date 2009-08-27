/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.HashMap;

import x10.util.concurrent.atomic.AtomicInteger;

import x10.io.Console;

/**
 * @author tardieu
 */
class FinishStates {

	private val map = new HashMap[RID, FinishState]();
	private val count = new AtomicInteger(0);
	private val lock = new Lock();

	def put(finishState:FinishState):Void {
        if (finishState.rid().id == -1) {
            lock.lock();
            if (finishState.rid().id == -1) {
                val rootFinish = finishState as RootFinish;
                rootFinish.rid = new RID(here, count.getAndIncrement());
                map.put(rootFinish.rid, rootFinish);
            }
            lock.unlock();
        }
    }
    
    def get(rid:RID):FinishState {
        lock.lock();
        val finishState = map.getOrElse(rid, null);
        if (null != finishState) {
            lock.unlock();
            return finishState;
        }
        val remoteFinish = new RemoteFinish(rid);
        map.put(rid, remoteFinish);
        lock.unlock();
        return remoteFinish;
    }
    
    def findRoot(rid:RID):RootFinish {
        lock.lock();
        val finishState = map.getOrElse(rid, null);
        lock.unlock();
        return finishState as RootFinish;
    }
    
    def removeRoot(rootFinish:RootFinish):Void{
        if (rootFinish.rid.id != -1) {
            lock.lock();
            map.remove(rootFinish.rid);
            lock.unlock();
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
