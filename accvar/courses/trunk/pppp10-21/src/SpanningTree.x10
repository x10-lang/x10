import x10.util.Random;
public class SpanningTree {
	
	static class V(id:Int) {
		var edges:Rail[V]=null;
		var out:V=null;
	    def this(id:Int) {
	    	property(id);
	    }
	    def set(e:Rail[V]) {
	    	edges=e;
	    }
	    public def mark() {
	    	out=this;
	    	finish visit();
	    }
	    def first(v:V) {
	    	var prev:V;
	    	atomic {
	    		prev=out;
	    		out=out==null || out.id > v.id ? v : out;
	    	}
	    	return prev==null;
	    }
	    public def visit() {
	    	for (v in edges)  
	    		if (v.first(this)) async
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
	    	"" + id + " (" + edgeString() + ") > " + (out==null ? "null" : "" + out.id);
	}
	public static def main(args:Array[String](1)) {
		val N = Int.parseInt(args(0));
		Console.OUT.println("N=" + N);
		val vertices = Rail.make(N, (i:Int)=> new V(i));
		val r = new Random();
		for (v in vertices) {
			val N1 = Math.min(Math.abs(r.nextInt(N))+1, 4);
			v.set(Rail.make[V](N1, (i:Int)=> 
				vertices(i==0? (v.id == N-1? N-1 : v.id+1)
						: Math.abs(r.nextInt(N)))));
		}
		Console.OUT.println("Starting mark.");
		val time = System.nanoTime();
		vertices(0).mark();
		Console.OUT.println("Time: " + (System.nanoTime()-time)*1.0/(1000*1000*1000) + " s");
		for (v in vertices) {
			Console.OUT.println(""+v);
		}
	}
}