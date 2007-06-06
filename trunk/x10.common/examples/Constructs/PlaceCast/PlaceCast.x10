/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//BadPlaceExceptions not being thrown correctly.
import harness.x10Test;

public class PlaceCast extends x10Test {
	int nplaces = 0;

	public boolean run() {
		final dist d = dist.factory.unique(place.places);
		System.out.println("num places = " + place.places);
		final BoxedBoolean[:distribution==d] disagree
			= new BoxedBoolean[d] (point [p]) {
				System.out.println("The currentplace is:" + here);
				return new BoxedBoolean();
			};
		finish ateach (final point [p]: d) {
			// remember if here and d[p] disagree
			// at any activity at any place
			try {
				final place q = d[p].next();
				BoxedBoolean!here x = (BoxedBoolean!q) disagree[p];
				async(this) { atomic { nplaces++; } }
			} catch (BadPlaceException x)  {
				System.out.println("Caught bad place exception for " + p);
			}
		}
		System.out.println("nplaces == " + nplaces);
		return nplaces == 0;
	}

	public static void main(String[] args) {
		new PlaceCast().execute();
	}

	static class BoxedBoolean {
		boolean val = false;
	}
}

