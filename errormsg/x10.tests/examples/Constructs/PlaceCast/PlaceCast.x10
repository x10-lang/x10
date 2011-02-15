/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
import x10.io.Console;
public class PlaceCast extends x10Test {
    var nplaces: int = 0;
    private val root=GlobalRef[PlaceCast](this);
    public def run()  {
	  val d: Dist = Dist.makeUnique();
	  Console.OUT.println("num places = " + Place.MAX_PLACES);
	  val disagree = DistArray.make[BoxedBoolean](d, (Point)=> new BoxedBoolean());
	  val root = this.root;
	  finish ateach (p in d) {
	      // remember if here and d[p] disagree
	      // at any activity at any place
	      try {
		    val q  = d(p).next();
		    // Always throws ClassCastException
		    val x = disagree(p).root as GlobalRef[BoxedBoolean]{self.home==q};
		    at (root) atomic root().nplaces++; 
	      } catch (x: ClassCastException) {
	    	Console.OUT.println("Caught class cast exception for " + p);
	      }
	  }
	  Console.OUT.println("nplaces == " + nplaces);
	  return nplaces == 0;
    }

    public static def main(Array[String](1)) {
	  new PlaceCast().execute();
    }

	static class BoxedBoolean {
		private val root=GlobalRef[BoxedBoolean](this);
	   var v: boolean = false;
	}
}
