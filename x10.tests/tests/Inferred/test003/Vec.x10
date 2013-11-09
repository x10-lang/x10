/**
 * Example from Vijay.
 *
 */



import x10.compiler.InferGuard;

class Vec(len: Long) {
    static def add (x: Vec, y: Vec{ self.len == x.len }) {}
    @InferGuard
    static def add2 (x: Vec, y: Vec) { /*??< y.len == x.len >??*/ } { // Here we would like to infer the constraint
        add(x, y);  // <=  x.len == y.len
    }
}
