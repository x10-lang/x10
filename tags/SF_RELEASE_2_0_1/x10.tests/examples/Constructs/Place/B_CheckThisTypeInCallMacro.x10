/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 * Testing that thisType is correctly updated and propagated into the code to check a
 * method call. and that R(this.rank)! is processed correctly.
 * 
 * @author vj
 */
class R(rank:Int) {
	static type R(r:int)=R{self.rank==r};
	static type Test(r:int)=Test{self.rank==r};
    def check( tt:Test(this.rank)) {}       
    def this(r:Int){property(r);}
  }
class Test(rank:Int) {
	static type Test(r:int)=Test{self.rank==r};
	static type R(r:int)=R{self.rank==r};
    def this(r:Int){property(r);}
    var r:R(this.rank)! =null;
    def m(t:Test(this.rank)) {
         r.check(t);
    }
}
public class B_CheckThisTypeInCallMacro extends x10Test {
    public def run() = true;
    public static def main(Rail[String]) {
	  new 
	  B_CheckThisTypeInCallMacro().execute();
    }

}
