// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_51 extends x10Test {

    // standin for Region
    static public class R {
        incomplete public static def $convert(rs: ValRail[R]): R;
        incomplete public def $or(r:R):R;
    }
    
    // standin for val r:Region = [a..b, c..d]
    val r:R = [new R(), new R()];
    
    // this works as expected
    val w = r || r;
    
    // this works
    // standin for val z = r || [a..b, c..d]
    val x = r || [new R(), new R()];
    
    // this doesn't work
    // standin for val w = [a..b, c..d] || r
    val y = [new R(), new R()] || r;
    
    // this doesn't work
    // standin for val y = [a..b, c..d] || [a..b, c..d]
    val z = [new R(), new R()] || [new R(), new R()];

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_51().execute();
    }
}
