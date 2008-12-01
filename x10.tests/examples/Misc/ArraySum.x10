import x10.io.Console;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public class ArraySum {

    var sum: int;
    val size: int;
    val data: Rail[int];
    val R:Region{rail};

    public def this(n: int): ArraySum = {
        size=n;
        R= 0..n-1 to Region{rail};
        data = Rail.makeVar[int](n, (x:nat)=>0 to int);
        for (var i: int = 0; i < n; i++) data(i)=1;
        sum=0;
    }

    def sum(a: Rail[int], start: int, last: int): int = {
        var mySum: int = 0;
        for (var i: int = start; i < last; i++) mySum += a(i);
        return mySum;
    }

    def sum(val numThreads: int): void = {
        val mySize: int = size/numThreads;
        finish foreach ((p) in 0..numThreads-1) {
            var mySum: int = sum(data, p*mySize, (p+1)*mySize);
            atomic sum += mySum;
        }
    }
    
    public static def main(args: Rail[String]): void = {

        var size: int = 5*1000*1000;
        if (args.length >=1)
            size = Int.parseInt(args(0));

        println("initializing");
        var a: ArraySum = new ArraySum(size);
        val numThreads = [1,2,4];

        //warmup loop
        println("doing warmup");
        for (var i: int = 0; i < numThreads.length; i++) 
            a.sum(numThreads(i));
        
        for (var i: int = 0; i < numThreads.length; i++) {
            println("starting with " + i + " threads");
            a.sum=0;
            var time: long = - System.nanoTime();
            a.sum(numThreads(i));
            time += System.nanoTime();
            println("For p=" + numThreads(i) 
                    + " result: " + a.sum 
                    + ((size==a.sum)? " ok" : "  bad") 
                    + " (time=" + (time/(1000*1000)) + " ms)");
        }
        
        
    }

    @Native("java", "System.out.println(#1)")
    @Native("c++", "printf(\"%s\\n\", (#1)->c_str()); fflush(stdout)")
    static native def println(x:String):void;
}
