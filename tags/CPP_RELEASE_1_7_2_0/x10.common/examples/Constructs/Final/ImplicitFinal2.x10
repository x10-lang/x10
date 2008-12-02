/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing new implicit final rule (lack thereof).
 * @author kemal, 5/2005
 */
public class ImplicitFinal2 extends x10Test {

	public boolean run() {
		point p = [1,2,3];
		region r = [10:10];
		point p1 = [1+1,2+2,3+3];
		dist d = r->here;
		p = [1,2,4];
		r = [10:11];
		p1 = [1+1,2+2,4+4];
		d = [0:1]->here;
		point P = [1,2,3];
		region R = [10:10];
		dist D  = R->here;
		P = p;
		R = r;
		D  = d;
		int A = 1;
		A = A + 1;
		int Bb = 1;
		Bb = Bb + 1;
		int BB = 1;
		BB = BB + BB;
		int c = 1;
		c = Bb + c;
		return true;
	}

	public static void main(String[] args) {
		new ImplicitFinal2().execute();
	}
}

