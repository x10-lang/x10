package ssca2;

public struct  Consts {
	public val SCALE: Types.INT_T;
public val N: Types.LONG_T;
public val M: Types.LONG_T;
public val MaxIntWeight: Types.WEIGHT_T;
public val SubGraphPathLength: Types.INT_T;
public val K4Approx: Types.INT_T;
public val nthreads: Types.INT_T;
public def this(scale: Types.INT_T, n: Types.LONG_T, m: Types.LONG_T, maxwt: Types.WEIGHT_T, sublength: Types.INT_T, k4: Types.INT_T, nthreads: Types.INT_T) {
	SCALE = scale;
	N = n;
	M = m;
	MaxIntWeight = maxwt;
	SubGraphPathLength = sublength;
	K4Approx = k4;
	this.nthreads = nthreads;
}
};


