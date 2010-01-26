/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.io.Console;
public class PlaceCast extends x10Test {
    var nplaces: int = 0;

    public def run()  {
	  val d: Dist = Dist.makeUnique();
	  Console.OUT.println("num places = " + Place.MAX_PLACES);
	  val disagree = Array.make[BoxedBoolean](d, (Point)=> new BoxedBoolean());
	  finish ateach (p in d) {
	      // remember if here and d[p] disagree
	      // at any activity at any place
	      try {
		    val q  = d(p).next();
		    // Always throws ClassCastException
		    val x = disagree(p) as (BoxedBoolean!q);
		    at (this) atomic nplaces++; 
	      } catch (x: ClassCastException) {
	    	Console.OUT.println("Caught class cast exception for " + p);
	      }
	  }
	  Console.OUT.println("nplaces == " + nplaces);
	  return nplaces == 0;
    }

    public static def main(Rail[String]) {
	  new PlaceCast().execute();
    }

	static class BoxedBoolean {
	   var v: boolean = false;
	}
}
