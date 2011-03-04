import x10.io.Console;
import harness.x10Test;

public class ArraySum extends x10Test {

    var sum: int;
    val size: int;
    val data: Rail[int];
    val R:Region{rail};

    public def this(n: int)  {
        size=n;
        R = 0..n-1 as Region{rail};
        data = Rail.makeVar[int](n, (x:nat)=>1);
        sum=0;
    }

    def sum(a: Rail[int], start: int, last: int): int = {
        var mySum: int = 0;
        for (var i: int = start; i < last; i++) mySum += a(i);
        return mySum;
    }

    def sum(val numThreads: int) {
        val mySize: int = size/numThreads;
        finish foreach ((p):Point(1) in 0..numThreads-1) {
            var mySum: int = sum(data, p*mySize, (p+1)*mySize);
            atomic sum += mySum;
        }
    }
    
    public def run(): boolean {
        var size: int = 5*1000;
        var a: ArraySum = new ArraySum(size);
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
