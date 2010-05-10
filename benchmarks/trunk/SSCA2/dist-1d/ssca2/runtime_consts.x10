package ssca2;

public struct runtime_consts {
	public val SCALE: types.INT_T;
public val N: types.LONG_T;
public val M: types.LONG_T;
public val MaxIntWeight: types.WEIGHT_T;
public val SubGraphPathLength: types.INT_T;
public val K4Approx: types.INT_T;
public def this(scale: types.INT_T, n: types.LONG_T, m: types.LONG_T, maxwt: types.WEIGHT_T, sublength: types.INT_T, k4: types.INT_T) {
	SCALE = scale;
	N = n;
	M = m;
	MaxIntWeight = maxwt;
	SubGraphPathLength = sublength;
	K4Approx = k4;
}
};


