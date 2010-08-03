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

package x10.compiler;

/**
 * @author igor
 */
public final class RemoteOperation {
    // FIXME: HACK
    @Native("c++", "x10rt_remote_op((#1)->FMGL(id), (x10rt_remote_ptr) &(((#2)->raw())[(#3)]), X10RT_OP_XOR, #4)")
    public static def xor(p:Place, r:Rail[Long]/*!p*/, i:Int, v:Long) {
        async (p) {
            (r as Rail[Long]!)(i) ^= v;
        }
    }

    @Native("c++", "x10rt_remote_op((#1), (x10rt_remote_ptr) &(((#2)->raw())[(#3)]), X10RT_OP_XOR, #4)")
    public static def xor(id:Int, r:Rail[Long]/*!p*/, i:Int, v:Long) {
        async (Place(id)) {
            (r as Rail[Long]!)(i) ^= v;
        }
    }

    @Native("java", "x10.x10rt.X10RT.fence()")
    @Native("c++", "x10rt_remote_op_fence()")
    public static def fence() { }
}

// vim:shiftwidth=4:tabstop=4:expandtab
