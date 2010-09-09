/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing that an at spawned at some other place cannot access a remote field.
 */
public class AtFieldWrite_MustFailCompile extends x10Test {
	var t: T;
public def run() {
	val Second = Place.FIRST_PLACE.next();
	val newT = new T();
	at (Second) { 
		this.t = newT; 
	}
return true;
}

public static def main(Rail[String]) {
	new AtFieldWrite_MustFailCompile().execute();
}

static class T {
	public var i: int;
}
}
