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

package x10.lang;

import x10.compiler.Inline;
import x10.util.IndexedMemoryChunk;

public class Rail[T](size:Int) implements Iterable[T] {
    private val raw:IndexedMemoryChunk[T];

    public @Inline def raw() = raw;

    public @Inline def this() {
        property(0);
        raw = IndexedMemoryChunk.allocateUninitialized[T](0);
    }

    public @Inline def this(size:Int){T haszero} {
        property(size);
        raw = IndexedMemoryChunk.allocateZeroed[T](size);
    }

    public @Inline def this(size:Int, init:T) {
        property(size);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Int=0; i<size; ++i) raw(i) = init;
    }

    public @Inline def this(size:Int, init:(Int)=>T) {
        property(size);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Int=0; i<size; ++i) raw(i) = init(i);
    }

    public @Inline operator this(index:Int):T = raw(index);

    public @Inline operator this(index:int)=(v:T):T{self==v} {
        raw(index) = v;
        return v;
    }

    public def iterator():Iterator[T] = new Iterator[T]() {
        var cur:Int = 0;
        public def next():T = raw(cur++);
        public def hasNext() = cur < size;
    };
}
