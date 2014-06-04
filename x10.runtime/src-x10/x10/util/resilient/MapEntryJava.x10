/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util.resilient;

import x10.interop.Java;
import x10.util.Box;

import java.util.Map;

/**
 * A map entry key-value pair.  This class is based on Java's Map.Entry interface.
 * It is used when an X10 program needs to access a Java-created MapEntry object.
 */

public class MapEntryJava {

    var javaMapEntry: java.util.Map.Entry;


    public def this(mapEntry: java.util.Map.Entry) {
      javaMapEntry = mapEntry;
    }

    /**
     * Return the key corresponding to this entry.
     */
    public def getKey(): Any {
        return javaMapEntry.getKey() as Any;
    };

    /**
     * Return the value corresponding to this entry.
     */
    public def getValue(): Any {
        return Java.deserialize(javaMapEntry.getValue() as Java.array[Byte]) as Any;
    };

    /**
     * Replaces the value corresponding to this entry with the specified value.  Return previous value.
     */
    public def setValue(value: Any): Any {
        return Java.deserialize(javaMapEntry.setValue(value) as Java.array[Byte])  as Any;
    };


}
