/**
 * Example from Vijay.
 *
 */

package test004;

import x10.compiler.InferGuard;

class Vec(len: Int) {
    static def copy (x: Vec, y: Vec{ self.len == x.len }) {}

    @InferGuard
    static def cp (x: Vec, l: Int) { // l should be a local variable to infer
        /* val l <: Int  = ?; */
        val y = new Vec(l); //  =>  y.len == l
        copy(x, y);         // <=   x.len == y.len
        return y;
    }
}
