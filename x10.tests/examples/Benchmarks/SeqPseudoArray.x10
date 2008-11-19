public class SeqPseudoArray extends Benchmark {

    //
    // parameters
    //

    val N = 2000;

    def expected() = 1.0;

    def operations() = N*N to double;


    //
    // the benchmark
    //

    final static value Arr {

        val m0: int;
        val m1: int;
        val raw: Rail[double];
        
        def this(m0:int, m1:int) {
            this.m0 = m0;
            this.m1 = m1;
            this.raw = Rail.makeVar[double](m0*m1);
        }
        
        final def set(v:double, i0: int, i1: int) {
            raw(i0*m1+i1) = v;
        }
        
        final public def apply(i0:int, i1: int) {
            return raw(i0*m1+i1);
        }
    }
    
    val a = new Arr(N, N);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                a.set(a.apply(i,j)+1, i,j);
        return a.apply(20,20);
    }

    //
    // boilerplate
    //

    def this(args:Rail[String]) {
        super(args);
        reference("snakehead", "java",             1.63155e+08);
        reference("snakehead", "x10-opt-java",     1.07302e+07);
    }

    public static def main(args:Rail[String]) {
        new SeqPseudoArray(args).execute();
    }
}
