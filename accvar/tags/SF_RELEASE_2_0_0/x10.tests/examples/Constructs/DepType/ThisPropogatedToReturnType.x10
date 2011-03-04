/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/** Tests that if t is of type Test(:i==1), and test() on Test returns int(:self==this.i),
* then t.test() returns int(:self==1). That is, information is propagating
* correctly from the type of the receiver into the return type of a method.
* @author vj
*/
public class ThisPropogatedToReturnType extends x10Test {

	 class Test(i:int, j:int) {
		def this(i:int, j:int):Test{self.i==i&&self.j==j} = { 
			property(i,j);
			}
		
		def test():int{self==this.i} = {
			return  i;
		}
	}
	
	public def run(): boolean = {
		var t: Test!{i==1} = new Test(1,2);
		var one: int{self==1} = t.test();
		return true;
	}
	public static def main(Rail[String]): void = {
		 new ThisPropogatedToReturnType().execute();
	}
}
