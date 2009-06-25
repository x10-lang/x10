/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

public class PlaceCast2 extends x10Test {
	var nplaces: int = 0;
	public def run(): boolean = {
		val d: dist = dist.factory.unique(place.places);
		System.out.println("num places = " + place.places);
		val disagree: Array[BoxedBoolean]{dist==d} = new Array[BoxedBoolean](d, ((p): point): BoxedBoolean => {
				System.out.println("The currentplace is:" + here);
				return new BoxedBoolean();
			});
		finish ateach (val (p): point in d) {
			// remember if here and d[p] disagree
			// at any activity at any place
			try {
				var x: BoxedBoolean = disagree(p) as BoxedBoolean!d(p);
				async(this) { atomic { nplaces++; } }
			} catch (x: BadPlaceException)  {
				System.out.println("Caught bad place exception for " + p);
			}
		}
		System.out.println("nplaces == " + nplaces);
		return nplaces == place.places.size();
	}

	public static def main(var args: Rail[String]): void = {
		new PlaceCast2().execute();
	}

	static class BoxedBoolean {
		var v: boolean = false;
	}
}
