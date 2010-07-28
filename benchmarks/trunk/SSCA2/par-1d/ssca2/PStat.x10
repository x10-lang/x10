package ssca2;
import x10.util.*;
final public class PStat extends AbsStat[Double] {
	
	static val stats: List[AbsStat[Double]]!  = new ArrayList[AbsStat[Double]]();
	global val op: (Double, Double)=>Double;
	
	def this (name: String, op: (Double, Double)=>Double): PStat {
		super(name);   
		this.op = op;
	} 
	
	public static def make (name: String, op:(Double, Double)=>Double): PStat! {
		val stat = new PStat(name,  op);
		stats.add(stat);
		return stat;
	}
	
	
	public global def elapsed() {
		return acc(here.id);
	}
	
	public global def event_count(count: Double) {
		acc(here.id) = op(acc(here.id), count);
		//    x10.io.Console.OUT.println(" event : " + count +" " + acc(here.id));
	}
	public global def print() =  x10.io.Console.OUT.println("PStat " + name + " Count: " + acc(here.id));
	
	public static def resetAll()  = AbsStat.resetAll[Double](stats);
	public static def printAll() = AbsStat.printAll[Double](stats);
	public static def printDetailed() = AbsStat.printDetailed[Double](stats, 1.0);
	
	
}
