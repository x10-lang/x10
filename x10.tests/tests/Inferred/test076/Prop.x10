

import x10.compiler.InferGuard;

class Prop {
    static property eq0(x:Long) = x == 0;

    static def assert0(x: Long){ Prop.eq0(x) }{}

    @InferGuard
    static def f (y:Long{/*??< self==0 >??*/}) {
    	assert0(y);
    }

}
