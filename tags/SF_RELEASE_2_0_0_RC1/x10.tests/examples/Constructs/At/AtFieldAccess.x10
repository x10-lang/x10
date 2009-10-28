/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing an at spawned to a field access.
 */
public class AtFieldAccess extends x10Test {

	var t: T;
	public def run() {
		var Second:Place = Place.FIRST_PLACE.next();
		var r: Region = 0..0;
		val D = r->Second;
		for (p: Point in D.region) {
			t = at (D(p)) new T();
		}
		val tt = this.t;
		at (tt) tt.i = 3;
		return 3 == (at(tt) tt.i);
	}

	public static def main(Rail[String]) {
		new AtFieldAccess().execute();
	}

	static class T {
		public var i: int;
	}
}
