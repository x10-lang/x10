/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Sara Salem Hamouda 2014-2015.
 */
package x10.util.resilient.iterative;

import x10.util.HashMap;

/**
 * A snapshot of an object, providing methods to copy objects locally and remotely.
 */
public interface Snapshot {
    public abstract def clone():Any;
    public abstract def remoteCopyAndSave(key:Any, hm:PlaceLocalHandle[HashMap[Any,Any]], backupPlace:Place):void;
    public abstract def remoteClone(targetPlace:Place):GlobalRef[Any]{self.home==targetPlace};
}
