/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */
package x10.util.resilient;

import x10.util.HashMap;

/**
 * A snapshot of an object, providing methods to copy to a remote place in a
 * variety of different ways.
 */
public interface Snapshot {
    public abstract def clone():Any;
    public abstract def remoteCopyAndSave(key:Any, hm:GlobalRef[HashMap[Any,Any]]):void;
    public abstract def remoteClone(targetPlace:Place):GlobalRef[Any]{self.home==targetPlace};
}
