

import x10.compiler.*;
import x10.glb.Context;
import x10.util.Team;
import x10.glb.GLBResult;
import x10.glb.TaskQueue;
import x10.glb.TaskBag;
public final class Queue extends UTS implements TaskQueue[Queue, Long] {
	public def this(factor:Int) {
		super(factor);
	}
	
	@Inline public def process(n:Long, context:Context[Queue, Long]) {
		var i:Long=0;
		for (; (i<n) && (size>0); ++i) {
			expand();
		}
		count += i;
		return size > 0;
	}
	
	@Inline public def split() {
		var s:Long = 0;
		for (var i:Long=0; i<size; ++i) {
			if ((upper(i) - lower(i)) >= 2) ++s;
		}
		if (s == 0) return null;
		val bag = new Bag(s);
		s = 0;
		for (var i:Long=0; i<size; ++i) {
			val p = upper(i) - lower(i);
			if (p >= 2n) {
				bag.hash(s) = hash(i);
				bag.upper(s) = upper(i);
				upper(i) -= p/2n;
				bag.lower(s++) = upper(i);
			}
		}
		return bag;
	}
	
	@Inline public def merge(bag:Bag) {
		val h = bag.size();
		val q = size;
		while (h + q > hash.size) grow();
		Rail.copy(bag.hash, 0, hash, q, h);
		Rail.copy(bag.lower, 0, lower, q, h);
		Rail.copy(bag.upper, 0, upper, q, h);
		size += h;
	}
	
	@Inline public def merge(bag:TaskBag) {
		merge(bag as Bag);
	}
	
	// override
	public def printLog(){
		
	}
	
	@Inline public def count() = count;
	
	
	
	var result:UTSResult = null;
	public def getResult(): UTSResult {
		val result = new UTSResult();
		return result;
		
		
			
	}
		
	public class UTSResult extends GLBResult[Long]{
		r:Rail[Long] = new Rail[Long](1l);		
		public  def getResult(): Rail[Long]{
			r(0) = count;
			return r;
		}
		public  def getReduceOperator():Int{
			return Team.ADD;
		}
		
		public def display(r:Rail[Long]):void{
			Console.OUT.println("Myresult: "+r(0));
		}
	}
		
	
	
	
	
	
}
