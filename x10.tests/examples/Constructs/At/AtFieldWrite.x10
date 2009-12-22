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
public class AtFieldWrite extends x10Test {
	var t: T;
public def run() {
  val Second = Place.FIRST_PLACE.next();
  val newT = at (Second) new T();
  at (Second) { 
	newT.i = 3; 
  }
  return true;
}

public static def main(Rail[String]) {
	new AtFieldWrite().execute();
}

static class T {
	public var i: int;
}
}
