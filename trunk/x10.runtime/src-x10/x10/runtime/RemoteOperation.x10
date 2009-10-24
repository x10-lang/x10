/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.compiler.Native;

/**
 * @author igor
 */
public final class RemoteOperation {
    // FIXME: HACK
    @Native("c++", "x10rt_remote_xor(#1,((((x10_ulong)x10aux::get_remote_ref((#2).operator->())) + sizeof(x10::lang::Rail<x10_long>)+7)&~0x7) + ((#3)*sizeof(x10_long)),#4)")
    public static def xor(p:Place, r:Rail[Long]/*!p*/, i:Int, v:Long) {
        async (p) {
            (r as Rail[Long]!)(i) ^= v;
        }
    }
}
