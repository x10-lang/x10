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

import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NativeRep;


/*
 * Note: this class is not part of API. Only used by generated Java code of Managed X10.
 */
public abstract class IndexedMemoryChunk__NativeRep {

    @Native("java", "(#4)._copyToLocal(#5,#6,#7,#8)")
    private static def copyToLocal[T](src:IndexedMemoryChunk[T], srcIndex:int, dst:IndexedMemoryChunk[T], dstIndex:int, numElems:int):void {}

    @Native("java", "(#4)._copyFromLocal(#5,#6,#7,#8)")
    private static def copyFromLocal[T](dst:IndexedMemoryChunk[T], dstIndex:int, src:IndexedMemoryChunk[T], srcIndex:int, numElems:int):void {}

    public static def copyTo[T](src:IndexedMemoryChunk[T], srcIndex:int,
                                dstPlace:Place, dst:IndexedMemoryChunk[T], dstIndex:int,
                                numElems:int,
                                uncounted:boolean):void {
        if (dstPlace == here) {
            copyToLocal(src, srcIndex, dst, dstIndex, numElems);
        } else {
            at (dstPlace) {
                // TODO copy between different Places
                for (var i: Int = 0; i < numElems; i++) {
                    dst(dstIndex + i) = src(srcIndex + i);
                }
            }
        }
    }

    public static def copyFrom[T](dst:IndexedMemoryChunk[T], dstIndex:int, 
                                  srcPlace:Place, src:IndexedMemoryChunk[T], srcIndex:int, 
                                  numElems:int, 
                                  uncounted:boolean):void {
        if (srcPlace == here) {
            copyFromLocal(dst, dstIndex, src, srcIndex, numElems);
        } else {
            // TODO copy between different Places
            for (var i: Int = 0; i < numElems; i++) {
                dst(dstIndex + i) = src(srcIndex + i);
            }
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
