/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class CheckDescendEntails extends x10Test {
	class Prop(i:int, j:int) {
		public def this(i: int, j: int): Prop{self.i==i&&self.j==j} = {
			property(i,j);
		}
		}
    class Test(a:Prop, b:Prop) {
        public def this(a:Prop, b:Prop): Test{self.a==a&&self.b==b} = {
        	property(a,b);
        }
     }
  
	public def run(): boolean = {
	   val p: Prop = new Prop(1,2);	
	   var t: Test{a == b} = new Test(p,p);
	   var u: Test{a.i == b.i} = t;
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new CheckDescendEntails().execute();
	}
}
