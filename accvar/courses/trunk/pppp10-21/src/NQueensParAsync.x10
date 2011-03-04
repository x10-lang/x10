public class NQueensParAsync {

    public static val expectedSolutions =
        [0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596, 2279184, 14772512];
    val N:Int;
    def this(N:Int) { this.N=N; }
    def start() {
        new Board().search();
    }
    class Board {
        val q: Rail[Int];
        def this() {
            q = Rail.make[Int](0, (Int)=>0);
        }
        def this(old: Rail[Int], newItem:Int) {
            val n = old.length;
            q = Rail.make[Int](n+1, (i:Int)=> (i < n? old(i) : newItem));
        }
        def safe(j: int) {
            val n = q.length;
            for (var k:int=0; k <n; ++k) {
                if (j == q(k) || Math.abs(n-k) == Math.abs(j-q(k)))
                    return false;
            }
            return true;
        }

        /** Search for all solutions in parallel.
         */
        def search(low:int, high:int) {
            for (var k:Int=low; k<= high; ++k) 
                if (safe(k)) {
                    val k_=k;
                    async new Board(q, k_).search();
                }
        }

        def search()  offers Int {
            if (q.length == N) {
                 offer 1;
                return;
            }
            search(0,N-1);
        }
    }

    static val sum = new Reducible[Int]() {
    	public def zero()=0;
    	public def apply(i:Int,j:Int)=i+j;
    };
    public static def main(args: Array[String](1))  {
    	val n = args.size > 0 ? Int.parseInt(args(0)) : 12;
    	Console.OUT.println("N=" + n);
    	
    	val nq = new NQueensParAsync(n);
    	var start:Long = -System.nanoTime();
    	val count = finish(sum){ nq.start();};
    	val result =count==expectedSolutions(nq.N);
    	start += System.nanoTime();
    	start /= 1000000;
    	Console.OUT.println("NQueensParAsync " + nq.N +
    			") has " + count+ " solutions" +
    			         (result? " (ok)" : " (wrong)") + " (t=" + start + "ms).");
    }
}
