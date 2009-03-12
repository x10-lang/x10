/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class PlaceCast2 extends x10Test {
	var nplaces: int = 0;
	public def run(): boolean = {
		val d: Dist = dist.factory.unique(Place.places);
		x10.io.Console.OUT.println("num places = " + Place.MAX_PLACES);
		val disagree: Array[BoxedBoolean]{dist==d} = Array.make[BoxedBoolean](d, ((p): Point): BoxedBoolean => {
				x10.io.Console.OUT.println("The currentplace is:" + here);
				return new BoxedBoolean();
			});
		finish ateach (val (p): Point in d) {
			// remember if here and d[p] disagree
			// at any activity at any place
			try {
				var x: BoxedBoolean = disagree(p) as BoxedBoolean!d(p);
				async(this.location) { atomic { nplaces++; } }
			} catch (x: BadPlaceException)  {
				x10.io.Console.OUT.println("Caught bad place exception for " + p);
			}
		}
		x10.io.Console.OUT.println("nplaces == " + nplaces);
		return nplaces == Place.places.size();
	}

	public static def main(var args: Rail[String]): void = {
		new PlaceCast2().execute();
	}

	static class BoxedBoolean {
		var v: boolean = false;
	}
}
