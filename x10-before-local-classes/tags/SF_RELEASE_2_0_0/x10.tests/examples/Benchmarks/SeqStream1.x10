/**
 * Version of Stream with a collection of local arrays implementing a
 * global array.
 */

public class SeqStream1 extends Benchmark {

    const alpha = 1.5;
    const beta = 2.5;
    const gamma = 3.0;

    const NUM_TIMES = 10;
    const PARALLELISM = 2;
    const localSize = 512*1024;

    public def operations() = 1.0 * localSize * PARALLELISM * NUM_TIMES;
    public def expected() = (localSize+1)*(alpha+gamma*beta);

    //
    //
    //

    val as = Rail.makeVal[Rail[double]!](PARALLELISM, (p:int) =>
        Rail.makeVar[double](localSize)
    );
    
    val bs = Rail.makeVal[ValRail[double]](PARALLELISM, (p:int) => 
        Rail.makeVal[double](localSize, (i:int)=>alpha*(p*localSize+i))
    );

    val cs = Rail.makeVal[ValRail[double]](PARALLELISM, (p:int)=>
        Rail.makeVal[double](localSize, (i:int)=>beta*(p*localSize+i))
    );

    public def once() {
        for (var p:int=0; p<PARALLELISM; p++) {
            x10.io.Console.OUT.println("p " + p);
            val a = as(p);
            val b = bs(p);
            val c = cs(p);
            for (var tt:int=0; tt<NUM_TIMES; tt++) // XTENLANG-311
                for (var i:int=0; i<localSize; i++)
                    a(i) = b(i) + gamma*c(i);
        }
        return as(1)(1);
    }

    //
    //
    //

    public static def main(Rail[String]) {
        new SeqStream1().execute();
    }

}
