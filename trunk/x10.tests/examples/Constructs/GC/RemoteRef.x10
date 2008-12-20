/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Create remote references and see if local GCs are 
 * keeping them alive
 */

public class RemoteRef extends x10Test {

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
	    next;
	}
	next;
	return res.success;
    }

    public def spawnRemoteTask(c:Clock, iterCount:Int, res:ResultHolder) {
	val v = new AnObject();
	async (here.next()) clocked(c) {
	    for (var i:int = 0; i<iterCount; i++) {
	        next;
	        async (v.location) v.f++; 
            }
	    at (res.location) { res.success = (at (v.location) v.f == iterCount); }
	    next;
	}
    }

    public static def main(var args: Rail[String]): void = {
        new RemoteRef().execute();
    }
}
