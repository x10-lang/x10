import x10.lib.stream.*;
/**
  A parallel evaluator for N-Queens.

  A network of cells is spawned, one for each (I,J). If a queen is placed
  on cell (I,J), then the cell checks to see that there is no other queen on
  the same column or left or right diagonal by writing a unique value constructed
  from (I,J) on each location associated with these axes, and reading it back. A cell
  with a queen on it is guaranteed to read a value other than the one it wrote iff there is 
  some other interfering queen on one of the axes.

  @author vj. 
  @comment Part of the final exam for the PPPP course, Columbia Fall 2009.
 */
class Board {
    var result:Boolean=true;
    var q:ValRail[Int];
    val N:Int;
    val col:Rail[int];
    val ldiag:Rail[int];
    val rdiag:Rail[int];
    var done:Boolean = false;
    def this(N:int) {
        this.N=N;
        this.col = Rail.makeVar(N, (Int):Int=>0);
        this.ldiag = Rail.makeVar(2*N, (Int):Int=>0);
        this.rdiag = Rail.makeVar(2*N, (Int):Int=>0);
        this.q = Rail.makeVar(N, (Int):Int=>0);
    }
    def evaluator(ix:Sink[ValRail[Int]]):Sink[Boolean] {
        val s = new SimpleStreamImp[Boolean]();
        async {
                val c = Clock.make();
                foreach((i,j) in [0..N-1, 0..N-1] as Region(2)) clocked(c) 
                   cell(i,j);
                try {
                    while(true) {
                        q = ix.get();
                        result = true;
                        next; // 1. announce q is initialized
                        next; // 2. let cells compute
                        next; // 3. wait for result to have right value
                        s.put(result);
                    } 
                } catch (StreamClosedException) {
                } finally {
                    s.close();
                    done=true;
                }
        }
        return s.sink();
    }
    def cell(i:Int, j:Int) {
        while(! done) {
            next; // 1. wait till q is initialized
            val active = q(i) == j;
            val ipj =i+j, imj = N+i-j;
            if (active) {
                col(i) = j;
                ldiag(imj) = ipj;
                rdiag(ipj) = imj;
            }
            next; // 2. wait for all other cells to write
            if (active) {
                if (col(i) != j) result=false;
                if (ldiag(imj) != ipj) result=false;
                if (rdiag(ipj) != imj) result=false;
            }
            next; // 3. announce result is set
        }
    }
    public static def main(Rail[String]) {
        val boards:ValRail[ValRail[Int]] = 
                Rail.makeVal(4, (i:Nat) =>
                i==0 ? [1,2,3,4] :
                        i==1 ? [1,4,2,3] : 
                                i==2 ? [2,4,1,3] 
                                        : [3,1,4,2]);
        val results = [ false, false, true, true];
        PrintSink.make(new Board(4)
                       .evaluator(new ValRailSource[ValRail[Int]](boards)));
    }
}
