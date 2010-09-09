// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * 
 */

public class OperatorSelector1 extends ClosureTest {
    const add = Double.$plus.(Double,Double);
    def m(x:(double,double)=>double):void={}
    public def run(): boolean = {
       m(add);
       return true;
    }

    public static def main(var args: Rail[String]): void = {
        new OperatorSelector1().execute();
    }
}
