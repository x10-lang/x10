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

package x10.array;

import x10.compiler.Global;
import x10.compiler.Native;
import x10.util.IndexedMemoryChunk;
import x10.util.RemoteIndexedMemoryChunk;

/**
 * A class that encapsulates sufficient information about a remote
 * array to enable DMA operations via Array.copyTo and Array.copyFrom
 * to be performed on the encapsulated Array.<p>
 *
 * Because copyTo and copyFrom are low-level interfaces that do not necessarily require
 * the source and destination arrays to have equivalent regions, the Region of the
 * array is not cached directly in the RemoteArray object (to minimize serialized bytes).
 * If the Region is actually needed, it can be retrieved by using the array GlobalRef
 * to return to the referenced array's home location and access its region.
 */
public class RemoteArray[T](home:Place, region:Region, size:Int) {} {
    val array:GlobalRef[Array[T]{self.region==this.region, self.size==this.size}]{self.home==this.home};
    val rawData:RemoteIndexedMemoryChunk[T];

    public property rank:Int = region.rank;

    public def this(a:Array[T])
      : RemoteArray[T]{self.home==here, self.region==a.region, self.size==this.size} {
        property(here, a.region, a.size);
        // cast needed as type of 'this' does not include {a.region==this.region, a.size==this.size} even though this is established by property statement
        val arr = a as Array[T]{self.region==this.region, self.size == this.size};
        // cast needed as type of 'this' does not include {here==this.home} even though this is established by property statement
        array = GlobalRef[Array[T]{self.region==this.region, self.size==this.size}](arr) as GlobalRef[Array[T]{self.region==this.region, self.size==this.size}]{self.home==this.home};
        rawData = RemoteIndexedMemoryChunk.wrap(a.raw());
    }

    public def this (gpu:Place, reg:Region, raw:RemoteIndexedMemoryChunk[T], raw_len:Int)
      : RemoteArray[T]{self.home==gpu, self.region==reg, self.size==reg.size()} {
        property(gpu, reg, reg.size());
        rawData = raw;
        @Native("c++", "") {
            array = (at (gpu) GlobalRef[Array[T]{self.region==this.region, self.size==this.size}](null)) as GlobalRef[Array[T]{self.region==this.region, self.size==this.size}]{self.home==this.home};
        }
    }

    public def equals(other:Any) {
        if (!(other instanceof RemoteArray[T])) return false;
        val oRA = other as RemoteArray[T];
        return oRA.array.equals(array);
    }

    @Native("cuda", "(#0).raw[#2] = (#1)")
    public operator this(i:Int)=(v:T) {here==home, rank==1} = array()(i)=v;

    public operator this(p:Point{self.rank==this.rank})=(v:T) {here==home} = {
        // todo: constraint bug! Cause: Method apply(i0: x10.lang.Int){x10.array.Array#this.rank==1}[]: T{x10.array.RemoteArray#this.array.home==x10.array.RemoteArray#this.home} in x10.array.Array[T{x10.array.RemoteArray#this.array.home==x10.array.RemoteArray#this.home}]{self.region==x10.array.RemoteArray#this.region, self.size==x10.array.RemoteArray#this.size, x10.array.RemoteArray#this.array.home==x10.array.RemoteArray#this.home} cannot be called with arguments (x10.array.Point{self.rank==arg4094461.rank}); Call invalid; calling environment does not entail the method guard.
        // return array()(p)=v;
        val arr = array();
        return arr(p)=v;
    }

    @Native("cuda", "(#0).raw[#1]")
    public operator this(i:Int) {here==home, rank==1} = array()(i);

    public operator this(p:Point{self.rank==this.rank}) {here==home} = array()(p);

    public operator this() {here==home} = array();

    public def hashCode() = array.hashCode();
}

/* This version is preferable, as it does not duplicate state from the global ref, but it does not work:
x10/array/RemoteArray.x10:72: This or super cannot be used (implicitly or explicitly) in a property initializer.    
Expr: new x10.lang.GlobalRef[x10.array.Array[T]{self.region==x10.array.RemoteArray#this.region, self.size==x10.array.RemoteArray#this.size}](...)

public class RemoteArray[T](region:Region, size:Int, array:GlobalRef[Array[T]{self.region==this.region, self.size==this.size}]) {
    val rawData:IndexedMemoryChunk[T];

    public property rank:Int = region.rank;

    public def this(a:Array[T]) : RemoteArray[T]{self.region==a.region, self.size==this.size, self.array.home == here} {
        property(a.region, a.size, GlobalRef[Array[T]{self.region==this.region, self.size==this.size}](a as Array[T]{self.region==this.region, self.size == this.size}));
        rawData = a.raw();
    }

    public def equals(other:Any) {
        if (!(other instanceof RemoteArray[T])) return false;
        val oRA = other as RemoteArray[T];
        return oRA.array.equals(array);
    }

    public operator this(i:Int)=(v:T) {here==array.home, rank==1} = array()(i)=v;

    public operator this(p:Point{self.rank==this.rank})=(v:T) {here==array.home} = array()(p)=v;

    public operator this(i:Int) {here==array.home, rank==1} = array()(i);

    public operator this(p:Point{self.rank==this.rank}) {here==array.home} = array()(p);

    public def hashCode() = array.hashCode();
}
*/

