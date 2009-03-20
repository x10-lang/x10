/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing new implicit final rule (lack thereof).
 * @author kemal, 5/2005
 */
public class ImplicitFinal2 extends x10Test {

	public def run(): boolean = {
		var p: point = [1, 2, 3];
		var r: region = [10..10];
		var p1: point = [1+1, 2+2, 3+3];
		var d: dist = r->here;
		p = [1, 2, 4];
		r = [10..11];
		p1 = [1+1, 2+2, 4+4];
		d = [0..1]->here;
		var P: point = [1, 2, 3];
		var R: region = [10..10];
		var D: dist = R->here;
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
