// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

import x10.util.concurrent.atomic.AtomicInteger;

class XTENLANG_238 extends x10Test {


    public def run(): boolean {
        val a = new AtomicInteger(0);
        a.incrementAndGet();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_238().execute();
    }
}
