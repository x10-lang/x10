
import x10.glb.ArrayListTaskBag;
import x10.glb.TaskQueue;
import x10.glb.TaskBag;
import x10.glb.GLBParameters;
import x10.glb.GLB;
import x10.util.Team;
import x10.glb.Context;
import x10.glb.ContextI;
import x10.glb.GLBResult;

/**
 * An illustration of the use of the GLB framework. 
 * The state of a Fib task is represented by a Long. 
 * Each Task Frame maintains its task bag in an array list.
 */
public class FibG(n:Long) {
	static def fib(n:Long):Long=n<2? n: fib(n-1)+fib(n-2);
	
	class FibTQ implements TaskQueue[FibTQ, Long]{
		val bag = new ArrayListTaskBag[Long]();
		var result:Long=0;
		
		public def getTaskBag()=bag;
		
		public def init(n:Long) {
			bag.bag().add(n);
		}
		//public def process(var n:Long, context:Context[FibTQ, Long]):Boolean {
		public def process(var n:Long, context:Context[FibTQ, Long]):Boolean {
			val b = bag.bag();
			var yieldI:Long = 0L;
			for (var i:Long=0; bag.size() > 0 && i < n; i++) {
				val x = b.removeLast(); // constant time
				if (x < 2) result += x;
				else {
					b.add(x-1); // constant time
					b.add(x-2);
				}
				yieldI++;
				if(yieldI >= 1L){
					yieldI = 0L;
					context.yield();
					
				}
			}
			return b.size()>0;
			
		}
		
		
		
		public def count() {
			
			return 0L;
		}
		
		public def merge(var _tb:TaskBag):void {
			this.bag.merge(_tb as ArrayListTaskBag[Long]);
		}
		
		
		public def split(): TaskBag {
			return this.bag.split();
		}
		
		public def reduce():void {
			result= Team.WORLD.allreduce(result,Team.ADD); 
		}
		
		public def printLog(){
			// do nothing
		}
		
		// override
		var fr:FibResult = null;
		public def getResult():FibResult{
			if(fr == null){
				fr = new FibResult();
			}
			return fr;
		}
		
		class FibResult extends GLBResult[Long]{
			var r:Rail[Long] = null;
			
			
			public def getResult():x10.lang.Rail[x10.lang.Long] {
				
					r = new Rail[Long](1l);
					r(0) = result;
					Console.OUT.println("local result: " + r(0));
				
				return r;
			}
			
			public def getReduceOperator():x10.lang.Int {
				// TODO: auto-generated method stub
				return Team.ADD;
			}
			
			
			
			public def display(r:Rail[Long]):void {
				// TODO: auto-generated method stub
				Console.OUT.println("Inside display:");
				Console.OUT.println("Myresult: "+ r(0));
			}
			
			
			
		}
		
		
	}
	
	
	
	//var finalResult:Long;
	public def run(n:Long):Rail[Long] {
		// val g = new GlobalLoadBalancer[Long](GLBParameters.Default, GlobalLoadBalancer.BALANCED_LEVEL_NUB);
		// return g.run(()=>new FibFrame());
		val init = ()=>{ return new FibTQ(); };
		
		val glb = new GLB[FibTQ, Long](init, GLBParameters.Default, true);
		
		Console.OUT.println("Starting...");
		var time:Long = System.nanoTime();
		val start = ()=>{ (glb.taskQueue()).init(n); };
		return glb.run(start);
		
		//  Place.places().broadcastFlat(()=>{
		// 	(glb.taskQueue()).reduce();
		//   
		// });
		// 
		// time = System.nanoTime() - time;
		// Console.OUT.println("Finished.");
		// Console.OUT.println("finalResult:" + glb.taskQueue().result);
		
		
	}
	public static def main(args:Rail[String]) {
	    val N = args.size < 1 ? 10 : Long.parseLong(args(0));
	    val result = mainTest(args);
		//return result;
		//Console.OUT.println("fib(" + N + ")" + fib(N));
	     Console.OUT.println("fib-glb(" + N + ")" + result(0));
		
	}

    public static def mainTest(args:Rail[String]):Rail[Long] {
		val N = args.size < 1 ? 10 : Long.parseLong(args(0));
		val result = new FibG(N).run(N);
		return result;
		//Console.OUT.println("fib(" + N + ")" + fib(N));
		//Console.OUT.println("fib-glb(" + N + ")" + result(0));
		
	}

	
}
