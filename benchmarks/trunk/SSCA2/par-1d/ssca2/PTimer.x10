package ssca2;
import x10.util.*;
public class PTimer extends AbsStat[Long]{
	
	static val timers: List[AbsStat[Long]]!  = new ArrayList[AbsStat[Long]]();
	
	def this (name: String): PTimer {
		super(name);
	} 
	
	public static def make (name: String): PTimer! {
		val timer = new PTimer(name);
		timers.add(timer);
		return timer;
	}
	
	public global def  start(): void {
		start(here.id) = Timer.nanoTime();
	}
	
	public global def stop() {
		acc(here.id) += Timer.nanoTime() - start(here.id);
	}
	
	public global def print() =  x10.io.Console.OUT.println("PTimer " + name + " Seconds: " + acc(here.id)*1e-9);
	
	public static def resetAll()  = AbsStat.resetAll[Long](timers);
	public static def printAll() = AbsStat.printAll[Long](timers);
	public static def printDetailed() = AbsStat.printDetailed[Long](timers, 1e-9);
	
}

