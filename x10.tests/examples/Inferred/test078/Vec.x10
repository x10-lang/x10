/**
 * Using constraint comming from return type
 *
 */



import x10.compiler.InferGuard;

class Vec(len: Long) {
    static def add (x: Vec, y: Vec){ x.len == y.len }: Vec{ self.len == x.len } {
	val v = new Vec(x.len);
	return v;
    }

    @InferGuard
    static def add3 (x: Vec, y: Vec, z: Vec) {
	return add(add(x, y), z);
    }
}
