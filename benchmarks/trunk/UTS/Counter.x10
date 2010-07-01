/**
 * A Counter object tracks statistics associated with one place in a UTS execution.
 * @author vj
 *
 */
public class Counter  {

	var lifelines:Long=0L;
	var lifelineNodes:Long=0L;
	var nodesCounter :Long= 0L;
	var nodesSent :Long= 0L;
	var stealsAttempted:Long = 0L;
	var stealsPerpetrated:Long = 0L;
	var stealsReceived:Long = 0L;
	var stealsSuffered:Long = 0L;
	var nodesGiven:Long = 0L;
	var nodesReceived:Long = 0L;
	var lastTimeStamp:Long=-1L;
	var timeComputing:Long = 0L;
	var timeAlive:Long = 0L;
	var timeDead:Long=0L;
	var chainDepth:Int=0;
	var maxDepth:Int=0;
	
	public def toVal() = new ValCounter(this);

	static class ValCounter {
		global val lifelines:Long;
	global val lifelineNodes:Long;
	global val nodesCounter :Long;
	global val nodesSent :Long;
	global val stealsAttempted:Long;
	global val stealsPerpetrated:Long;
	global val stealsReceived:Long;
	global val stealsSuffered:Long;
	global val nodesGiven:Long;
	global val nodesReceived:Long;
	global val lastTimeStamp:Long;
	global val timeComputing:Long;
	global val timeAlive:Long;
	global val timeDead:Long;
	global val chainDepth:Int;
	global val maxDepth:Int;
	def this(c:Counter) {
		lifelines = c.lifelines;
		lifelineNodes=c.lifelineNodes;
	nodesCounter =c.nodesCounter;
	nodesSent =c.nodesSent;
	stealsAttempted=c.stealsAttempted;
	stealsPerpetrated=c.stealsPerpetrated;
	stealsReceived=c.stealsReceived;
	stealsSuffered=c.stealsSuffered;
	nodesGiven=c.nodesGiven;
	nodesReceived=c.nodesReceived;
	lastTimeStamp=c.lastTimeStamp;
	timeComputing=c.timeComputing;
	timeAlive=c.timeAlive;
	timeDead=c.timeDead;
	chainDepth=c.chainDepth;
	maxDepth=c.maxDepth;
	}
	private def verboseStats(here:Int, sumCounters:Counter) {
		val P = Place.MAX_PLACES;
		val idealRatio = 1.0F/P;
		val pc = stealsAttempted==0L ? "NaN" : "" + safeSubstring("" + (100.0F*stealsPerpetrated)/stealsAttempted, 0,5);
		val pr = stealsReceived==0L ? "NaN" : "" +  safeSubstring("" + (100.0F*stealsSuffered)/stealsReceived, 0, 5);
		Console.OUT.println(here+": processed " + nodesCounter + " nodes.");
		val ratio = (1.0F*nodesCounter)/sumCounters.nodesCounter;
		val ratioS = safeSubstring(""+ratio,0,6);
		val imbalance = (100.0F*(ratio-idealRatio))/idealRatio;
		val imbalanceS = safeSubstring(""+ imbalance,0,4);
		Console.OUT.println("\t " + lifelines + " lifeline steals received "  
				+ lifelineNodes + " (total nodes).");
		Console.OUT.println("\t " + ratioS + " ratio, "  
				+ imbalanceS + "% imbalance.");
		Console.OUT.println("\t" + stealsPerpetrated+"/"+ stealsAttempted +"="
				+ pc +"% successful steals, received " 
				+ nodesReceived + " nodes.");
		Console.OUT.println("\t" + stealsSuffered+"/"+stealsReceived+"="
				+ pr + "% suffered, gave " 
				+ nodesGiven + " nodes.");
		Console.OUT.println("\t max launch depth=" + maxDepth);
		val total = timeAlive + timeDead;
		Console.OUT.println("\t Time: computing= " + timeComputing/1000 + " us ("
				+ safeSubstring("" + (100.0F*timeComputing)/total , 0, 5)+ "%)");
		Console.OUT.println("\t Time: stealing= " + (timeAlive - timeComputing)/1000 + " us ("
				+ safeSubstring("" +(100.0F*(timeAlive - timeComputing))/total, 0,5)+ "%)");
		Console.OUT.println("\t Time: idle= " + timeDead/1000 + " us ("
				+ safeSubstring("" + (100.0F*timeDead)/total,0,5) + "%)");
		Console.OUT.println("\t Time is "
				+ safeSubstring("" + (100.0F*(timeAlive+timeDead))/sumCounters.lastTimeStamp,
						0, 4)+ "% of max.");
	}

	}
	public def this() {}

	global safe public def toString():String {
		return lifelines + "," + lifelineNodes + ",nc:" + nodesCounter + ","
		+ stealsAttempted + "," + stealsPerpetrated + "," + stealsReceived + ","
		+ nodesGiven + "," + nodesReceived + "," + lastTimeStamp + ",ta:"
		+ timeAlive + ",td:" + timeDead + "," + maxDepth;
	}

	def incLifeline(n:Int) {
		this.lifelines++;
		lifelineNodes += n;
	}
	def incRxNodes(n:Int) {
		nodesReceived += n;
		stealsPerpetrated++;
	}
	def incTxNodes(n:Int) {
		nodesGiven += n;
		stealsSuffered++;
	}
	def incStealsReceived() {
		stealsReceived++;
	}
	def incStealsAttempted() {
		stealsAttempted++;
	}
	def updateDepth(depth:Int) {
		chainDepth=depth;
		maxDepth = max(chainDepth, maxDepth);
	}

	def incTimeComputing(t:Long) {
		timeComputing += t;
	}
	def incRx(lifeline:Boolean, n:Int) {
		if (lifeline) {
			incLifeline(n);
		} else {
			// sent from lifeline guy because it was immediately available.
			incRxNodes(n);
		}
	}
	def startLive() {
		var time:Long  = System.nanoTime();
		if (lastTimeStamp > 0) {
			timeDead += (time - lastTimeStamp);
		}
		lastTimeStamp= time;
	}
	def stopLive() {
		var time:Long = System.nanoTime();
		timeAlive += time-lastTimeStamp;
		lastTimeStamp = time;
	}
	
	static def abs(i:Float) =i < 0.0F ? -i : i;
	static def absMax(i:Float, j:Float) = abs(i) < abs(j) ? j : i;
	static def myMin(i:Float, j:Float) = (j==Float.NaN ? i : i < j ? i : j);
	static def max(i:Long, j:Long) = i > j ? i : j;
	def max(x:Int, y:Int) = x <= y? y : x;

	/**
	 * Print stats associated with this computation, communicating with other places to get their
	 * Counters.. 
	 * @param time
	 * @param verbose -- if details for each place should be printed.
	 */
	public def stats(st:PlaceLocalHandle[BinomialState], time:Long, verbose:Boolean) {
		assert here.id == 0;
		val P = Place.MAX_PLACES;
		val allCounters = Rail.make[ValCounter](P,(i:Int) => at(Place(i)) st().counter.toVal());
		val sumCounters = new Counter();
		for (b in allCounters) {
			sumCounters.addIn(b);
		}
		sumCounters.assertTxTally();
		Console.OUT.println("max alive+dead time ="  + sumCounters.lastTimeStamp + " ns");
		val nodeSum = sumCounters.nodesCounter;

		var balance:Float  = 0.0F;
		var minAliveRatio:Float =101.0F;
		var relativeAliveRatio:Float = 101.0F;
		val idealRatio = 1.0F/P;
		for (b in  allCounters) {
			val nodes = b.nodesCounter;
			val ratio = (1.0F*nodes)/nodeSum;
			val iBalance = ((100.0F*(ratio-idealRatio))/idealRatio);
			balance = absMax(balance, iBalance);
			val thisRatio = (100.0F*b.timeAlive)/(b.timeAlive+b.timeDead);
			minAliveRatio = myMin(minAliveRatio, thisRatio);
			relativeAliveRatio = myMin(relativeAliveRatio, (100.0F*(b.timeAlive+b.timeDead))/sumCounters.lastTimeStamp);
		}

		if (verbose)
			for (var i:Int=0; i < P; i++) {
				allCounters(i).verboseStats(i, sumCounters);
			}

		val stolenSum = sumCounters.nodesReceived;
		val steals = sumCounters.stealsPerpetrated;
		val llN = sumCounters.lifelineNodes;
		val ll = sumCounters.lifelines;
		Console.OUT.println("Overhead:\n\t" + (stolenSum+llN) + " total nodes stolen."); 
		val theftEfficiency = (stolenSum*1.0F)/steals;
		Console.OUT.println("\t" + safeSubstring("" + steals, 0,5)+ " direct steals."); 
		Console.OUT.println("\t" + safeSubstring("" + theftEfficiency, 0,8)+ " nodes stolen per attempt."); 
		Console.OUT.println("\t" + ll + " lifeline steals.");
		Console.OUT.println("\t" + safeSubstring("" + (1.0F*llN)/ll, 0,8) + " nodes stolen/lifeline steal.");
		Console.OUT.println("\t" + safeSubstring("" + balance, 0,6) + "% imbalance (max magnitude).");
		Console.OUT.println("\t" + safeSubstring("" + minAliveRatio, 0,6) + "% worst alive ratio.");

		Console.OUT.println("Performance = "+nodeSum+"/"+safeSubstring("" + (time/1E9), 0,6)
				+"="+ safeSubstring("" + (nodeSum/(time/1E3)), 0, 6) + "M nodes/s");

	}
	
	

	private static def safeSubstring(str:String, start:Int, end:Int) = str.substring(start, Math.min(end, str.length()));
	public def assertTxTally() {
		Console.OUT.println("Rx:" + (nodesReceived + lifelineNodes) + " Tx: "+ nodesGiven);
	}

	// Computing statistics
	public def addIn(other: ValCounter) {
		nodesCounter += other.nodesCounter;
		nodesReceived += other.nodesReceived;
		nodesGiven += other.nodesGiven;
		stealsPerpetrated += other.stealsPerpetrated;
		lifelines += other.lifelines;
		lifelineNodes += other.lifelineNodes;
		maxDepth = max(maxDepth, other.maxDepth);
		lastTimeStamp = max(lastTimeStamp, other.timeAlive+other.timeDead); // reuse lastTimeStamp
	}


}
