import x10.util.Random;
import x10.util.concurrent.atomic.AtomicBoolean;
public class GraphSumOptimized {
	
	static class V(id:Int) {
		var edges:Rail[V]=null;
		val visited:AtomicBoolean=new AtomicBoolean();
		val weight:Int;
	    def this(id:Int, weight:Int) {
	    	property(id);
	    	this.weight=weight;
	    }
	    def set(e:Rail[V]) {
	    	edges=e;
	    }
	    static val reducer = new Reducible[Int]() {
	    	public def zero()=0;
	    	public def apply(a:Int, b:Int)=a+b;
	    };
	    public def sum() = finish(reducer) {visit();};
	   
	    def visited():Boolean = visited.getAndSet(true);
	
	    public def visit() offers Int {
	    	offer weight;
	    	for (v in edges)  
	    		if (! v.visited()) async
	    			v.visit();
	    		
	    }
	    def edgeString() {
	    	if (edges==null) return "";
	    	var r:String = "";
	    	for (v in edges)
	    		r += " " + v.id;
	    	return r;
	    }
	    public def toString() = 
	    	"" + id + " (" + edgeString() + ") > " + weight;
	}
	public static def main(args:Array[String](1)) {
		val N = Int.parseInt(args(0));
		Console.OUT.println("N=" + N);
		val vertices = Rail.make(N, (i:Int)=> new V(i, i));
		val r = new Random();
		for (v in vertices) {
			val N1 = Math.min(Math.abs(r.nextInt(N))+1, 4);
			v.set(Rail.make[V](N1, (i:Int)=> 
				vertices(i==0? (v.id == N-1? N-1 : v.id+1)
						: Math.abs(r.nextInt(N)))));
		}
		Console.OUT.println("Workers: " + Runtime.INIT_THREADS);
		val time = System.nanoTime();
		Console.OUT.println("Sum is " + vertices(0).sum());
		Console.OUT.println("Time: " + (System.nanoTime()-time)*1.0/(1000*1000*1000) + " s");
		
	}
}