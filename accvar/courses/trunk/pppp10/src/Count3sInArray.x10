import x10.util.Random;

/**   
 *  Counts 3's in an array.
 */  

public class Count3sInArray {
    static val Meg = 1000*1000;
    static val indent = "\t";

    val size:Int;
    val pctThrees:Double;
    val numAsyncs:Int;
    var a:Rail[int]; 
    var count: int = 0;
    var expectedCount:int=0;
    var countTime: Long=0L;
    var populateTime: Long=0L;
    
    /**   
     *  Default constructor 
     */  
    public def this(size:Int, numAsyncs:Int, pctThrees:Double) {
        assert numAsyncs >= 1;
        this.size=size;
        this.numAsyncs = numAsyncs;
        this.a = Rail.make[int](size, 0);
        this.pctThrees = pctThrees;
    }
    
    public def run() {
        populate();

	// print out array before running
	/*
        val a_=this.a;
	Console.OUT.print(indent + "[ ");
        for ((i) in 0..size-1) {
	    Console.OUT.print(a_(i) + " ");
	}
	Console.OUT.println("]");
	*/
        val time = System.nanoTime();
        count = 0;
        if (numAsyncs == 1) {
            this.count = count3s();
        } else 
            finish for ((i)in 0..numAsyncs-1) async {
                val mc = count3s(i);
                atomic this.count += mc;
            }
        countTime += (System.nanoTime() - time)/Meg;
    }    

    public def runB() {
        populate();

        val time = System.nanoTime();
        count = 0;
        if (numAsyncs == 1) {
            this.count = count3s();
        } else {
        }
        countTime += (System.nanoTime() - time)/Meg;
    }    


    public def count3s(id:Int) {
        val chunkSize = size/numAsyncs;
        val start = chunkSize*id;
        // adjust final endpoint to account for size not being a mulitple of numAsyncs.
        val end = (id == numAsyncs-1) ? size-1 : start + chunkSize - 1;
	atomic Console.OUT.println(indent + "[task " + id + " counting from " + start + " to " + end + "]");
        var privateCount:Int=0;
        val a_=this.a;
        for (var i:int=start; i <=end ; i++)
            if(a_(i) == 3)
                privateCount++;
        
        return privateCount;
    }

    public def count3s():Int {
        val start = 0;
        val end = size-1;
        var privateCount:Int=0;
        val a_=this.a;
        for (var i:int=start; i <=end ; i++)
            if(a_(i) == 3)
                privateCount++;
        return privateCount;
    }

    public def populate()
    {
        val time = System.nanoTime();
        val r = new Random();
        expectedCount=0;
        val target = (a.length*pctThrees) as Int;
	Console.OUT.println(indent + "[target="+target+"]");
        // This loop messes up the cache and causes
        // countTime to increase about 40%.
        val a_=this.a;
        for ((i) in 0..a.length-1) a_(i)=0;
        for (var i:int=1; i <= target; i++) {
            val index = r.nextInt(a_.length);
            if (a_(index)!= 3) { // already assigned, skip
                a_(index) = 3; 
                expectedCount++;
            }
        }
        populateTime += (System.nanoTime() -time)/Meg;
    }
    
    public def validate() {
	Console.OUT.println(indent + "[expectedCount="+expectedCount+"]");
	Console.OUT.println(indent + "[count="+count+"]");
        if (expectedCount != count)
            Console.OUT.println(indent + "Error: expected " + expectedCount + ", obtained " + count + ".");
    }
    public def populateTime()=populateTime;
    public def countTime()=countTime;
    public def reset() {
        populateTime=0;
        countTime=0;
        count=0;
        expectedCount=0;
    }
    /**   
     *  Main method 
     */  
    public static def main(args:Rail[String]): Void {
        if (args.length < 3) {
            Console.OUT.println("Usage: Count3sInArray <Nmod4: size is 4*Nmod4> <NumAsync:Int> <PctThrees:Double>");
            return;
        }
        val Nmod4 = Int.parseInt(args(0));
        val size  = Nmod4 * 4;
        val numAsyncs = Int.parseInt(args(1));
        val pctThrees = Double.parseDouble(args(2));
	Console.OUT.println("[size=" + size + "]");
	Console.OUT.println("[numAsyncs=" + numAsyncs + "]");
	Console.OUT.println("[pctThrees=" + pctThrees + "]");
	val numInvoke = 5; // number of repetitions
	val counter = new Count3sInArray(size, numAsyncs, pctThrees);
	        
	//warmup
	Console.OUT.println("[Warming up]");
	counter.run();
	Console.OUT.println(indent + "[Done, time=" + counter.populateTime() + " + " + counter.countTime() + "].");
	counter.validate();
	counter.reset();
	        
	// now run the trial
	Console.OUT.println("[Running]");
	for((i) in 1..numInvoke) {
	    Console.OUT.println("[Trial #" + i + "]");
	    counter.run();
	    counter.validate();
	}
	Console.OUT.println("[Done. Over " + numInvoke + " trials, average time" 
			    + " to populate is  " + counter.populateTime()/numInvoke
			    + " and to count is " + counter.countTime()/numInvoke
			    + ".]");
	        
    }
	    
}
