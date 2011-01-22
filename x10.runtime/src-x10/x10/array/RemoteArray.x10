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
 */
public class RemoteArray[T](region:Region, size:Int, array:GlobalRef[Array[T]]) {} {
    val rawData:RemoteIndexedMemoryChunk[T];

    public property rank:Int = region.rank;
    public property home:Place = array.home;

    public def this(a:Array[T]) {
        property(a.region, a.size, GlobalRef[Array[T]](a));
        rawData = RemoteIndexedMemoryChunk.wrap(a.raw());
    }
    
    public def this(reg:Region, raw:RemoteIndexedMemoryChunk[T]) {
        val arr:GlobalRef[Array[T]];
        if (raw.home().isCUDA()) @Native("c++", "{}") {
            // This block will never be executed; only here to placate the X10-level typechecker
            arr = GlobalRef[Array[T]](null);
        } else {
            arr = at (raw.home()) GlobalRef[Array[T]](new Array[T](reg, raw()) as Array[T]);
        }
        property(reg, reg.size(), arr);
        rawData = raw;
    }

    public def equals(other:Any) {
        if (!(other instanceof RemoteArray[T])) return false;
        val oRA = other as RemoteArray[T];
        return oRA.array.equals(array);
    }

    @Native("cuda", "(#0).raw[#2] = (#1)")
    public operator this(i:Int)=(v:T) {here==array.home, rank==1} = this()(i)=v;

    public operator this(p:Point{self.rank==this.rank})=(v:T) {here==home} = {
        return this()(p)=v;
    }

    @Native("cuda", "(#0).raw[#1]")
    public operator this(i:Int) {here==array.home, rank==1} = this()(i);

    public operator this(p:Point{self.rank==this.rank}) {here==array.home} = this()(p);

    public operator this() {here==array.home} = (this.array)() as Array[T]{self.rank==this.rank};

    public def hashCode() = array.hashCode();
}
