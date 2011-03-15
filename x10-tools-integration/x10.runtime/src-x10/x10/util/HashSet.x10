/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;
import x10.io.CustomSerialization;
import x10.io.SerialData;
public class HashSet[T] extends MapSet[T] implements CustomSerialization {
    public def this() { super(new HashMap[T,boolean]()); }
    public def this(sz: int) { super(new HashMap[T,boolean](sz)); }
    public def serialize():SerialData = (map as HashMap[T,boolean]).serialize(); // Warning: This is an unsound cast because the object or the target type might have constraints and X10 currently does not perform constraint solving at runtime on generic parameters.
    def this(a:SerialData) { super(new HashMap[T,boolean](a)); }
    public def clone(): HashSet[T] = new HashSet[T](serialize());
}
