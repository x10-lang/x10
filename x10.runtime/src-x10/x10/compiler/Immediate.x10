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

package x10.compiler;

/**
 * A restricted form of an Uncounted async whose body
 * is not allowed to perform blocking operations. 
 * Therefore the body must not contain finish, at, or when.
 *
 * The runtime provides stronger progress guarantees for 
 * remote asyncs annotated as Immediate.  In particular the 
 * Runtime at each Place will always maintain at least one
 * worker thread that can execute Immediate asyncs as they are
 * dequeued from the network.
 */
public interface Immediate(suffix:String) extends Uncounted, 
                                                  RemoteInvocation {
}
