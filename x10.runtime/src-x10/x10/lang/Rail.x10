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

public final class Rail[T](size:Long) implements Iterable[T],(Int)=>T,(Long)=>T {

    public property def range() = new LongRange(0, size-1);

    // Iterable for now. Eventually rails will support comprehension loops without implementing Iterable
    public def iterator():Iterator[T] = new RailIterator[T](this);

    public def toString():String {
        val sb = new x10.util.StringBuilder();
        sb.add("[");
        val sz = Math.min(size, 10L);
        for (var i:Long=0L; i<sz; ++i) {
            if (i > 0L) sb.add(",");
            sb.add("" + raw(i));
        }
        if (sz < size) sb.add("...(omitted " + (size - sz) + " elements)");
        sb.add("]");
        return sb.toString();
    }

    // temporary implementation using IndexedMemoryChunk

    private val raw:IndexedMemoryChunk[T];

    public @Inline def raw() = raw;

    public @Inline def this(backingStore:IndexedMemoryChunk[T]) {
        property(backingStore.length() as Long);
        raw = backingStore;
    }

    // empty rail constructor
    public @Inline def this() {
        property(0L);
        raw = IndexedMemoryChunk.allocateUninitialized[T](0);
    }

    // unsafe constructor
    @Inline def this(Unsafe.Token, size:Long) {
        property(size);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
    }

    /* Construct a copy of the given Rail */
    @Inline def this(src:Rail[T]) {
        property(src.size);
        val size = src.size as Int; // TODO remove alias
        val dst = IndexedMemoryChunk.allocateUninitialized[T](size);
        // TODO use Long indices for IMC.copy
        IndexedMemoryChunk.copy[T](src.raw, 0, dst, 0, size);
        raw = dst;
    }

    // primary api: long indices

    public def this(size:Long){T haszero} {
        property(size);
        raw = IndexedMemoryChunk.allocateZeroed[T](size);
    }

    public @Inline def this(size:Long, init:T) {
        property(size);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Long=0L; i<size; ++i) raw(i) = init;
    }

    public @Inline def this(size:Long, init:(Long)=>T) {
        property(size);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Long=0; i<size; ++i) raw(i) = init(i);
    }

    public @Inline operator this(index:Long):T = raw(index);

    public @Inline operator this(index:Long)=(v:T):T{self==v} {
        raw(index) = v;
        return v;
    }

    public static def copy[T](src:Rail[T], dst:Rail[T]) {
        if (src.size != dst.size) throw new IllegalArgumentException("source and destination do not have equal size");
        IndexedMemoryChunk.copy(src.raw, 0, dst.raw, 0, src.raw.length());
    }

    public static def copy[T](src:Rail[T], srcIndex:Long, 
            dst:Rail[T], dstIndex:Long, numElems:Long) {
       for (var i:Long=0L; i<numElems; ++i) {
           dst(dstIndex + i) = src(srcIndex + i);
       }
    }

    public def clear(){T haszero} {
        raw.clear(0, size);
    }

    // secondary api: int indices

    public @Inline def this(size:Int){T haszero} {
        property(size as Long);
        raw = IndexedMemoryChunk.allocateZeroed[T](size);
    }

    public @Inline def this(size:Int, init:T) {
        property(size as Long);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Int=0; i<size; ++i) raw(i) = init;
    }

    public @Inline def this(size:Int, init:(Int)=>T) {
        property(size as Long);
        raw = IndexedMemoryChunk.allocateUninitialized[T](size);
        for (var i:Int=0; i<size; ++i) raw(i) = init(i);
    }

    public @Inline operator this(index:Int):T = raw(index);

    public @Inline operator this(index:int)=(v:T):T{self==v} {
        raw(index) = v;
        return v;
    }

    public static def copy[T](src:Rail[T], srcIndex:Int, 
            dst:Rail[T], dstIndex:Int, numElems:Int) {
        // TODO use Long indices for IMC.copy
        IndexedMemoryChunk.copy[T](src.raw, srcIndex, dst.raw, dstIndex, numElems);
    }
}

