import x10.io.Console;
import x10.util.Random;
/**   
 *  Counts 3's in an array in parallell.
 */  
public class Count3sInArray2 {
    const Meg = 1000*1000;
    val size:Int;
    var a:Rail[int]; 
    var count: int = 0;
    var expectedCount:int=0;
    val numAsyncs: Int;
    val sizePerAsync: Int;
    var countTime: Long=0L;
    var populateTime: Long=0L;

    /**   
     *  Default constructor 
     */  
     public def this(size:Int, numAsyncs: Int) {
	 this.size=size;
	 this.a = Rail.makeVar[int](size, (int)=>0);
	 this.numAsyncs = numAsyncs;
	 this.sizePerAsync = numAsyncs > 1 ? size/numAsyncs : size;
     }

    public def run() {
          populate();
          val time = System.nanoTime();
	  count = 0;
	  if (numAsyncs==0) // sequential
	      count3s();
	  else 
	      finish {
		  for(var i:int=0; i < numAsyncs-1; ++i) {
			  val ii=i;
		      async
			  count3s(ii);
		      }
		  count3s(numAsyncs-1);
	  }
          countTime += (System.nanoTime() - time)/Meg;
    }    

     public def count3s()
     {
	 var privateCount:Int=0;
	 val a_=this.a;
         for (var i:int=0; i <=size-1 ; i++)
	     if(a_(i) == 3)
		 privateCount++;
	     count = privateCount;
     }

     public def count3s(id: int)
     {
	 val start = sizePerAsync * id;
	 val end = sizePerAsync*(id+1)-1;
	 var privateCount:Int=0;
	 val a_=this.a;
         for (var i:int=start; i <=end ; i++)
	     if(a_(i) == 3)
		 privateCount++;
         atomic count += privateCount;
     }
     public def populate()
     {
	 val time = System.nanoTime();
    	 val r = new Random();
	 expectedCount=0;
	 val target = a.length*0.25 as Int;
	 // This loop messes up the cache and causes
	 // countTime to increase about 40%.
	 	 val a_=this.a;
	 //for ((i) in 0..a.length-1) a_(i)=0;
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
	if (expectedCount != count)
	    Console.OUT.println("Error: expected " + expectedCount + ", obtained " + count + ".");
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
	if (args.length < 2) 
	    Console.OUT.println("Usage: Count3sInArray2 <Size; should be multiple of 4> <NumAsyncs; >=0, 0 for serial >");
    	val size = Int.parseInt(args(0));
    	val numAsyncs = Int.parseInt(args(1));
    	val numInvoke = 5; // number of repetitions
	val counter = new Count3sInArray2(size, numAsyncs);

	//warmup
	Console.OUT.println("[Warming up]");
	counter.run();
	Console.OUT.println("[Done, time=" + counter.populateTime() + " + " + counter.countTime() + "].");
	counter.validate();
	counter.reset();

	// now run the trial
	Console.OUT.println("[Running]");
    	for((i) in 1..numInvoke) {
	    counter.run();
	    //	    counter.validate();
	}
    	Console.OUT.println("[Done. Over " + numInvoke + " trials, average time" 
			    + " to populate is  " + counter.populateTime()/numInvoke
			    + " and to count is " + counter.countTime()/numInvoke
			    + ".]");
    	
    }
   
}
