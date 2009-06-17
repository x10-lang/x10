/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package moldyn;

public class Random {

	public int iseed;
	public double v1, v2;

	public Random(int iseed, double v1, double v2) {
		this.iseed = iseed;
		this.v1 = v1;
		this.v2 = v2;
	}

	public double update() {
		double rand;
		double scale = 4.656612875e-10;

		int is1, is2, iss2;
		int imult = 16807;
		int imod = 2147483647;

		if (iseed <= 0) { iseed = 1; }

		is2 = iseed % 32768;
		is1 = (iseed-is2)/32768;
		iss2 = is2 * imult;
		is2 = iss2 % 32768;
		is1 = (is1*imult+(iss2-is2)/32768) % (65536);

		iseed = (is1*32768+is2) % imod;

		rand = scale * iseed;

		return rand;
	}

	public double seed() {
		double s, u1, u2, r;
		s = 1.0;
		do {
			u1 = update();
			u2 = update();

			v1 = 2.0 * u1 - 1.0;
			v2 = 2.0 * u2 - 1.0;
			s = v1*v1 + v2*v2;
		} while (s >= 1.0);

		r = Math.sqrt(-2.0*Math.log(s)/s);

		return r;
	}
}

