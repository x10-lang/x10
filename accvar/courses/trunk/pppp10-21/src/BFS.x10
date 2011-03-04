import x10.util.Random;
public class BFS {
	
	static class V(id:Int) {
		var edges:Rail[V]=null;
		var out:Int=-1;
	    def this(id:Int) {
	    	property(id);
	    }
	    def set(e:Rail[V]) {
	    	edges=e;
	    }
	    public def mark() {
	    	out=0;
	    	finish async 
	    		visit(Clock.make());
	    }
	    def first(v:V) {
	    	var prev:V;
	    	atomic 
	    	if (out < 0) {
	    		out = v.out+1;
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public def visit(c:Clock) {
	    	for (v in edges)  
	    		if (v.first(this))  async clocked(c) {
	    			next;
	    			v.visit(c);
	    		}	
	    }
	    def edgeString() {
	    	if (edges==null) return "";
	    	var r:String = "";
	    	for (v in edges)
	    		r += " " + v.id;
	    	return r;
	    }
	    public def toString() = 
	    	"" + id + " (" + edgeString() + ") > " + out;
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
		vertices(0).mark();
		for (v in vertices) {
			Console.OUT.println(""+v);
		}
	}
}