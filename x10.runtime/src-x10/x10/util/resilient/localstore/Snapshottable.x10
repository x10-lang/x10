/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */
package x10.util.resilient.localstore;

/**
 * A Snapshottable object provides methods to create a snapshot of its
 * (distributed) state and restore to a previous snapshotted state.
 */
public interface Snapshottable {
    public def makeSnapshot_local():Cloneable;
    public def restoreSnapshot_local(snapshot:Cloneable):void;
}
