import x10.util.Random;
import x10.util.concurrent.atomic.AtomicInteger;
public class HistogramAtomicInteger {
	
	// data rail to collect into histogram
	var data:Rail[Int]; 
	val size:Int;
	val maxVal:Int;
	
	// serially computed histogram (for checking parallel version)
	var serialHistogram:Array[Int](1);
	
	// parallelly computed histogram 
	var parallelHistogram:Array[AtomicInteger](1);
	
	// number of asyncs to use
	val numAsyncs:Int;
	
	// performance counters
	var populateTime:Long=0;
	var serialTime:Long=0;
	var parallelTime:Long=0;
	static val M = 1000*1000;
	
	public def this(size:Int, maxVal:Int, numAsyncs:Int) {
		assert size >= 1;
		assert maxVal >= 0;
		assert numAsyncs >= 1;
		this.size=size;
		this.maxVal = maxVal;
		this.numAsyncs = numAsyncs;
		this.data = Rail.make[int](size, 0);
		this.serialHistogram = new Array[Int](maxVal, 0);
		this.parallelHistogram = new Array[AtomicInteger](maxVal, (i:Int)=> new AtomicInteger(0));
	}
	
	public def run() {
		Console.OUT.println("\t[computing histogram serially]");
		serialCompute();
		Console.OUT.println("\t[computing histogram in parallel]");
		parallelCompute();
		Console.OUT.println("\t[checking]");
		check();	
		reset();
	}
	
	public def populate() {
		val time = System.nanoTime();
		val r = new Random(time);
		val data_=this.data;
		for ([i] in 0..data.length-1) data_(i) = r.nextInt(maxVal);
		populateTime += (System.nanoTime()-time)/M;
	}
	
	public def serialCompute() {
		val time = System.nanoTime();
		for ([i] in 0..data.length-1) serialHistogram(data(i))++;
		serialTime += (System.nanoTime()-time)/M;
	}
	
	public def parallelCompute() {
		val time = System.nanoTime();
		finish for ([i] in 0..numAsyncs-1) async {
			chunkCompute(i);
		}
		parallelTime += (System.nanoTime() - time)/M;
	}    
	
	public def chunkCompute(id:Int) {
		val chunkSize = size/numAsyncs;
		val start = chunkSize*id;
		val end = (id == numAsyncs-1) ? size-1 : start + chunkSize - 1;
		val data_=this.data;
		for (var i:int=start; i <=end ; i++) {
			parallelHistogram(data_(i)).incrementAndGet(); 
		}
	}
	
	public def check() {
		for ([i] in 0..maxVal-1) 
			if (parallelHistogram(i).get() != serialHistogram(i)) {
				Console.OUT.println("Error: Incorrect count of " + i 
						+ ": expected " + serialHistogram(i)
						+ ", found " + parallelHistogram(i));
				assert(false);
			}
	}
	
	public def populateTime()=populateTime;
	
	public def serialTime()=serialTime;
	
	public def parallelTime()=parallelTime;
	
	public def timerReset() {
		serialTime=0;
		parallelTime=0;
	}
	
	public def reset() {
		for ([i] in 0..serialHistogram.size-1) {
			serialHistogram(i)=0;
			parallelHistogram(i).set(0);
		}
	}
	
	/**   
	 *  Main method 
	 */  
	public static def main(args:Array[String](1)): Void {
		if (args.size < 4) {
			Console.OUT.println("Usage: Histogram <size of data rail> <max data value> <number of asyncs> <number of invocations>");
			return;
		}
		val histogram = new HistogramAtomicInteger(Int.parseInt(args(0)),
				Int.parseInt(args(1)),
				Int.parseInt(args(2)));
		
		// warm up
		Console.OUT.println("[Warm up run]");
		histogram.populate();
		histogram.run();
		histogram.timerReset();
		
		// now run the trial
		val numInvoke:Int = Int.parseInt(args(3));
		for([i] in 1..numInvoke) {
			Console.OUT.println("[Trial #" + i + "]");
			histogram.run();
		}
		Console.OUT.println("[Done.] Over " + numInvoke + " trials, average time" 
				+ " to populate is " + histogram.populateTime()
				+ ", to compute serially is " + histogram.serialTime()/numInvoke
				+ ", and to compute in parallel is " + histogram.parallelTime()/numInvoke);
	}
	
}
