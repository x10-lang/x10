/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing that fields can be assigned within the body of an instance method if there
 * is no intervening at to place-shift.
 * @author vj
 */
public class FieldWrite extends x10Test {
	var t: T;
    public def run() {
	  this.t = new T(); 
      return true;
    }

    public static def main(Rail[String]) {
	  new FieldWrite().execute();
    }

    static class T {
	  public var i: int;
    }
}
