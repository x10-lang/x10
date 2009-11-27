/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.io.Console;

/**
 * Create remote references and see if local GCs are 
 * keeping them alive
 */

public class RemoteRef extends x10Test {

    static val Debug = false;

    static class AnObject {
      var f:Int = 0;
    }

    static class ResultHolder {
       var success:Boolean = false;
    }

    public def run(): boolean = {
	val iterCount = 20;
	val c = Clock.make();
	val res = new ResultHolder();
	spawnRemoteTask(c, iterCount, res);
	for (var i:int=0; i<iterCount; i++) {
	    // TODO: Try to force GC to happen here by doing lots of allocation
	    if (Debug) Console.OUT.println("Local before next: "+i);
	    next;
	    if (Debug) Console.OUT.println("Local after next: "+i);
	}
	if (Debug) Console.OUT.println("Local: before last next");
	next;
	if (Debug) Console.OUT.println("Local: after last next");
	return res.success;
    }

    public def spawnRemoteTask(c:Clock, iterCount:Int, res:ResultHolder) {
	val v = new AnObject();
	async (here.next()) clocked(c) {
	    for (var i:int = 0; i<iterCount; i++) {
		if (Debug) Console.OUT.println("Remote before next: "+i);
	        next;
		if (Debug) Console.OUT.println("Remote after next: "+i);
	        at (v) v.f++; 
            }
	    if (Debug) Console.OUT.println("Remote: before last next");
	    at (res) { res.success = (at (v) v.f == iterCount); }
	    next;
	    if (Debug) Console.OUT.println("Remote: after last next next");
	}
    }

    public static def main(var args: Rail[String]): void = {
        new RemoteRef().execute();
    }
}
