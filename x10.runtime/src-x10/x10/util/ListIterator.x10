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

package x10.util;

public interface ListIterator[T] extends CollectionIterator[T] {
    public def hasNext():Boolean;
    public def next():T;
    public def nextIndex():Long;

    public def hasPrevious():Boolean;
    public def previous():T;
    public def previousIndex():Long;

    public def set(v:T):void;
    public def add(v:T):void;
}
