// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

public class XTENLANG_259 extends x10Test implements (int)=>int {

    public def apply(i:int) = 0;
    public def set(v:int, i:int) {}
        
    public def run() {
        this(0) = this(0) + 1;
        this(0)++;
        this(0)--;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_259().execute();
    }
}
