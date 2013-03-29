/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
import x10.array.*;

/**
 * Testing new implicit final rule (lack thereof).
 * @author kemal, 5/2005
 */
public class ImplicitFinal2 extends x10Test {

	public def run(): boolean = {
		var p: Point = [1, 2, 3];
		var r: Region = Region.make(10,10);
		var p1: Point = [1+1, 2+2, 3+3];
		var d: Dist = r->here;
		p = [1, 2, 4];
		r = Region.make(10,11);
		p1 = [1+1, 2+2, 4+4];
		d = Region.make(0,1)->here;
		var P: Point = [1, 2, 3];
		var R: Region = Region.make(10,10);
		var D: Dist = R->here;
		P = p;
		R = r;
		D  = d;
		var A: int = 1;
		A = A + 1;
		var Bb: int = 1;
		Bb = Bb + 1;
		var BB: int = 1;
		BB = BB + BB;
		var c: int = 1;
		c = Bb + c;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ImplicitFinal2().execute();
	}
}
