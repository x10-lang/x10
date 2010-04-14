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
    @Native("c++", "x10rt_remote_xor((#1)->FMGL(id),((((x10_ulong)x10aux::get_remote_ref((#2).operator->())) + sizeof(x10::lang::Rail<x10_long>)+7)&~0x7) + ((#3)*sizeof(x10_long)),#4)")
    public static def xor(p:Place, r:Rail[Long]/*!p*/, i:Int, v:Long) {
        async (p) {
            (r as Rail[Long]!)(i) ^= v;
        }
    }

    @Native("c++", "x10rt_remote_op_fence()")
    public static def fence() { }
}

// vim:shiftwidth=4:tabstop=4:expandtab
