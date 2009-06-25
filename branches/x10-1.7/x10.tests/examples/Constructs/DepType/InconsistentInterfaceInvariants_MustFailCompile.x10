/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */
public class InconsistentInterfaceInvariants_MustFailCompile extends x10Test { 

    public static interface Test (l:int, m:int){this.m == this.l} {
     public def put():int;
    }
    
    public static interface Test1{this.l == 0, this.m == 1}  extends Test { 
     public public def foo(): int;
    }
    
    public def run()=true;
   
	
    public static def main(var args: Rail[String]): void = {
        new InconsistentInterfaceInvariants_MustFailCompile().execute();
    }
   

		
}
