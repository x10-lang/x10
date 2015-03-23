/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Test numeric conversions.
 *
 * @author Salikh Zakirov 5/2011
 */
public class TestNumericConversion extends x10Test {

    def ascending(from:Float, a:Float, to:Float) {
	if (!(from <= a) || !(a <= to))
	    return false;
	else
	    return true;
    }
    def ascending(from:Double, a:Double, to:Double) {
	if (!(from <= a) || !(a <= to))
	    return false;
	else
	    return true;
    }

    public def run(): boolean {
	var r : Boolean = true;
	if (1 as Float != 1.0f) 	{ p(" (1 as Float != 1.0f)    "); r = false; }
	if (0 as Float != 0.0f) 	{ p(" (0 as Float != 0.0f)    "); r = false; }
	if (-1 as Float != -1.0f) 	{ p(" (-1 as Float != -1.0f)  "); r = false; }
	if (1l as Float != 1.0f) 	{ p(" (1l as Float != 1.0f)   "); r = false; }
	if (0l as Float != 0.0f) 	{ p(" (0l as Float != 0.0f)   "); r = false; }
	if (-1l as Float != -1.0f) 	{ p(" (-1l as Float != -1.0f) "); r = false; }
	if (1ul as Float != 1.0f) 	{ p(" (1ul as Float != 1.0f)  "); r = false; }
	if (0ul as Float != 0.0f) 	{ p(" (0ul as Float != 0.0f)  "); r = false; }

	if (!ascending(4.294967168e9f, 0xFFFFffffu as Float, 4.294967295e9f))
	    { p(" (!ascending(4.294967168e9f, 0xFFFFffffu as Float, 4.294967295e9f)) "); r = false; }
	if (!ascending(1.8446743e19f, 0xFFFFffffFFFFfffful as Float, 1.8446744e19f))
	    { p(" (!ascending(1.8446743e19f, 0xFFFFffffFFFFfffful as Float, 1.8446744e19f)) "); r = false; }

	if (1 as Double != 1.0) 	{ p(" (1 as Double != 1.0)    "); r = false; }
	if (0 as Double != 0.0) 	{ p(" (0 as Double != 0.0)    "); r = false; }
	if (-1 as Double != -1.0) 	{ p(" (-1 as Double != -1.0)  "); r = false; }
	if (1l as Double != 1.0) 	{ p(" (1l as Double != 1.0)   "); r = false; }
	if (0l as Double != 0.0) 	{ p(" (0l as Double != 0.0)   "); r = false; }
	if (-1l as Double != -1.0) 	{ p(" (-1l as Double != -1.0) "); r = false; }
	if (1ul as Double != 1.0) 	{ p(" (1ul as Double != 1.0)  "); r = false; }
	if (0ul as Double != 0.0) 	{ p(" (0ul as Double != 0.0)  "); r = false; }

	if (0xFFFFffffu as Double != 4294967295.0) { p(" (0xFFFFffffu as Double != 4294967295.0) "); r = false; }
	if (!ascending(1.844674407370955e19, 0xFFFFffffFFFFfffful as Double, 1.844674407370956e19))
	    { p(" (!ascending(1.844674407370955e19, 0xFFFFffffFFFFfffful as Double, 1.844674407370956e19)) "); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new TestNumericConversion().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
