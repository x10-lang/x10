/**
 * Example from Vijay.
 *
 */



import x10.compiler.InferGuard;

class Vec(len: Long) {
    static def copy (x: Vec, y: Vec{ self.len == x.len }) {}

    @InferGuard
    static def cp (x: Vec, l: Long) { // l should be a local variable to infer
        /* val l <: Long  = ?; */
        val y = new Vec(l); //  =>  y.len == l
        copy(x, y);         // <=   x.len == y.len
        return y;
    }
}
