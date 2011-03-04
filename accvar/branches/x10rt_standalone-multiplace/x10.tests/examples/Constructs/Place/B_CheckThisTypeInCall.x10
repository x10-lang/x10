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
 * method call.
 * 
 * @author vj
 */
public class B_CheckThisTypeInCall extends x10Test {
	class Test(rank:Int) {
       class R(rank:Int) {
         def check( tt:Test{self.rank==this.rank}) {}       
         def this(r:Int){property(r);}
       }
       def this(r:Int){property(r);}
       var r:R!{self.rank == this.rank} =null;
       def m(t:Test{self.rank==this.rank}) {
         r.check(t);
       }
	}

    public def run() = true;
    

    public static def main(Rail[String]) {
	  new 
	  B_CheckThisTypeInCall().execute();
    }

}
