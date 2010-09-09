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
	public def run(): boolean = {
		var Second: Place = Place.FIRST_PLACE.next();
		var r: Region = [0..0];
		val D: Dist = r->Second;
		for (val p: Point in D.region) {
			val NewT: T = new T();
			at (D(p)) { t = NewT; }
		}
		at (t.location) { t.i = 3; }
		return 3 == (future(t.location) t.i).force();
	}

	public static def main(var args: Rail[String]): void = {
		new AtFieldAccess().execute();
	}

	static class T {
		public var i: int;
	}
}
