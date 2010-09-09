import x10.io.Console;
import harness.x10Test;

public class ArraySum extends x10Test {

    var sum: Int;
    val size: Int;
    val data: Rail[Int];
    val R:Region{rail};

    public def this(n: Int) {
        size=n;
        R= 0..n-1 as Region{rail};
        data = Rail.make[Int](n, (x:nat)=>1);
        // for ((i) in R) S executes S for each point in R.
        // R must be a 1-d region. (i) decomposes the 1-d point
        // to retrieve the index in the 0th dimension.
        // Thus for iteration over a 2d point, you would use
        // the idiom for ((i,j) in R) S
        // The syntax for (p in R) S will also work, but p
        // will be bound to the points in R. 
        sum=0;
    }

    def sum(a: Rail[Int]!, start: Int, last: Int): Int = {
        var mySum: Int = 0;
        for ((i) in start..last-1) mySum += a(i);
        return mySum;
    }

    def sum(numThreads: Int) {
        val mySize = size/numThreads;
        finish foreach ((p) in 0..numThreads-1) {
            val mySum = sum(data, p*mySize, (p+1)*mySize);
            // Multiple activities will simultaneously update
            // this location -- so use an atomic operation.
            atomic sum += mySum;
        }
    }
    
    public def run(): boolean {
        var size: int = 5*1000;
        var a:ArraySum! = new ArraySum(size);
        val numThreads = [1,2,4];
        var good:boolean=true;
        for (var i: int = 0; i < numThreads.length && good; i++) {
            a.sum=0;
            var time: long = - System.nanoTime();
            a.sum(numThreads(i));
            time += System.nanoTime();
        good &= size==a.sum;
        }
        return good;
    }
   
    public static def main(var args: Rail[String]){
        new ArraySum(1).execute();
    }
}
