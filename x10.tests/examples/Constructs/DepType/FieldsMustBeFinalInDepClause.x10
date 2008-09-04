/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vj
 */
public class FieldsMustBeFinalInDepClause extends x10Test {
	class Test(i:int, c:boolean) {
	   val b:boolean{self==c};
	   public def this(ii:int, bb:boolean):Test = {
	     property(ii,bb);
	     b=bb;
	   }
	   def m():Test{self.c==this.b}=this;
	}
	
	public def run(): boolean = {
	   val a= new Test(52,true);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new FieldsMustBeFinalInDepClause().execute();
	}
}
