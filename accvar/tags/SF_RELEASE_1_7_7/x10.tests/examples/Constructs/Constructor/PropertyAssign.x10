/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class PropertyAssign extends x10Test {
	class Prop(i: int, j: int) {
		public def this(i: int, j: int): Prop = {
			property(i,j);
		}
        }
  
	public def run(): boolean = {
                var p: Prop = new Prop(1,2);
	        return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PropertyAssign().execute();
	}
}
