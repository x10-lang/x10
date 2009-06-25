/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//BadPlaceExceptions not being thrown correctly.
import harness.x10Test;;

public class PlaceCast extends x10Test {
	var nplaces: int = 0;

	public def run(): boolean = {
		val d: dist = Dist.makeUnique(place.places);
		System.out.println("num places = " + place.places);
		val disagree: Array[BoxedBoolean]{distribution==d} = new Array[BoxedBoolean](d, ((p): point): BoxedBoolean => {
				System.out.println("The currentplace is:" + here);
				return new BoxedBoolean();
			});
		finish ateach (val (p): point in d) {
			// remember if here and d[p] disagree
			// at any activity at any place
			try {
				val q: place = d(p).next();
				var x: BoxedBoolean = disagree(p) as (BoxedBoolean!q);
				async(this) { atomic { nplaces++; } }
			} catch (var x: BadPlaceException)  {
				System.out.println("Caught bad place exception for " + p);
			}
		}
		System.out.println("nplaces == " + nplaces);
		return nplaces == 0;
	}

	public static def main(var args: Rail[String]): void = {
		new PlaceCast().execute();
	}

	static class BoxedBoolean {
		var v: boolean = false;
	}
}
