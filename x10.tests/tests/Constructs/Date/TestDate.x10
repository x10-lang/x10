/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.util.Date;

/**
 * Unit tests for x10.util.Date
 */
public class TestDate extends x10Test {

    public def run(): boolean {
        val tick = new Date();
        val x = tick.toString();

        System.sleep(1);

        val tock = new Date();
        val y = tick.toString();

        chk(tock.after(tick), "tock.after(tick)");
        chk(tick.before(tock), "tick.before(tock)");

        tock.setTime(tick.getTime());
        chk(tick.equals(tock), "tick.equals(tock)");
        chk(tick.toString().equals(tock.toString()), "date strings same");

        return true;
    }

    public static def main(args: Rail[String]) {
        new TestDate().execute();
    }

}
