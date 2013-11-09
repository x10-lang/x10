/**
 * Variation on the example Test04.
 *
 * Here, the value of l seems to depend on z which is introduced after l.
 * In fact z depends on constant.
 *
 */



import x10.compiler.InferGuard;

class Vec(len: Long) {
    static def copy (x: Vec, y: Vec{ self.len == x.len }) {}

    @InferGuard
    static def cp (x: Vec, l: Long) { // l should be a local variable to infer
        /* val l <: Long  = ?; */
         val y = new Vec(l);    //  =>  y.len == l
         val z = new Vec(42);   //  =>  z.len == 42
         copy(x, z);            // <=   x.len == z.len
         copy(z, y);            // <=   z.len == y.len
         return y;
    }
}
