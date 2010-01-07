/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.concurrent.atomic.AtomicInteger;
import x10.util.HashMap;

/**
 * @author tardieu
 */
class FinishStates implements (RootFinish)=>RemoteFinish {

    private val map = new HashMap[RootFinish, RemoteFinish!]();
    private val lock = new Lock();

    public def apply(rootFinish:RootFinish):RemoteFinish {
        lock.lock();
        val finishState = map.getOrElse(rootFinish, null);
        if (null != finishState) {
            lock.unlock();
            return finishState;
        }
        val remoteFinish = new RemoteFinish();
        map.put(rootFinish, remoteFinish);
        lock.unlock();
        return remoteFinish;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
