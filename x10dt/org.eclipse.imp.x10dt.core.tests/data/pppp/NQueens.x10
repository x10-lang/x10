import x10.io.Console;

public class NQueens {
    var nSolutions:int = 0;
    public static val expectedSolutions =
        [0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596, 2279184, 14772512];
    val N:Int;
    def this(N:Int) { this.N=N;}
    def start() {
        new Board().search();
    }
    class Board {
        val q: Rail[Int];
        def this() {
            q = Rail.makeVar[Int](0, (Nat)=>0);
        }
        def this(old: Rail[Int], newItem:Int) {
            val n = old.length;
            q = Rail.makeVar[Int](n+1, (i:Nat)=> (i < n? old(i) : newItem));
        }
        def safe(j: int) {
            val n = q.length;
            for ((k) in 0..n-1) {
                if (j == q(k) || Math.abs(n-k) == Math.abs(j-q(k)))
                    return false;
            }
            return true;
        }
        def search(R: Region(1)) {
            for ((k) in R)
                if (safe(k))
                    new Board(q, k).search();
        }

        def search()  {
            if (q.length == N) {
                nSolutions++;
                return;
            }
            search(0..N-1);
        }
    }

    public static def main(args: Rail[String])  {
    	val n = args.length > 0 ? Int.parseInt(args(0)) : 8;
    	println("N=" + n);
    	
    	val nq = new NQueens(n);
    	var start:Long = -System.nanoTime();
    	nq.start();
    	val result = nq.nSolutions==expectedSolutions(nq.N);
    	start += System.nanoTime();
    	start /= 1000000;
    	println("NQueens " + nq.N + " has " + nq.nSolutions + " solutions" +
    			(result? " (ok)." : " (wrong).") + "time=" + start + "ms");
    }

    static def println(s:String) = Console.OUT.println(s);
}
