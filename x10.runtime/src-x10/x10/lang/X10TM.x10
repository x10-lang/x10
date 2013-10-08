package x10.lang;

import x10.compiler.Native;

public class X10TM {
	static val MAX_THREADS = 50;
	static val x10tm_objs = new Array[X10TM](MAX_THREADS);
	
	public var tm_tid:int = -1;
	public val places = new Array[Place](10);
	public var num_places:int = 0;
	
	@Native("c++", "x10tm::tm_thread_get_result(#tid)")
	public static native def getResult(tid:int):int;
	
	@Native("c++", "x10tm::tm_thread_abort(#tid)")
	public static native def abort(tid:int):int;
	
	public static def getTMObj(tid:int) {
		if (X10TM.x10tm_objs(tid) == null) {
			X10TM.x10tm_objs(tid) = new X10TM(tid);
		}
		
		return X10TM.x10tm_objs(tid); 
	}
	
	public static def addPlace(tid:int, p:Place) {
		val obj = getTMObj(tid);
		obj.addPlace(p);
	}
	
	public static def finishCommits(tid:int) {
		val obj = getTMObj(tid);
		obj.finishCommits();
	}
	
	public static def failCommits(tid:int) {
		val obj = getTMObj(tid);
		obj.failCommits();
	}
	
	public def this(tid:int) {
		this.tm_tid = tid;
	}
	public def addPlace(p:Place) {
		for (var i:int=0; i < num_places; i++) {
			if (places(i) == p) {
				return;
			}
		}
		
		places(num_places) = p;
		num_places++;
		//Console.OUT.println("addPlace " + p);
	}
	
	public def finishCommits() {
		for (var i:int=0; i < num_places; i++) {
			//Console.OUT.println("finishCommit " + places(i));
			at (places(i)) {
				Runtime.finishTMCommit();
			}
		}
		num_places = 0;
	}
	
	public def failCommits() {
		for (var i:int=0; i < num_places; i++) {
			//Console.OUT.println("failCommit " + places(i));
			at (places(i)) {
				Runtime.failTMCommit();
			}
		}
		num_places = 0;
	}
}