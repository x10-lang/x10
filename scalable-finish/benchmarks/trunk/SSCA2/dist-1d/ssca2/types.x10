package ssca2;

import x10.util.*;

public class types {
	static type INT_T =  Int;
	static type DOUBLE_T = double;
	static type LONG_T = Int; //x10.lang.Long; /* 64 bit */
	static type WEIGHT_T = LONG_T;
	static type VERT_T =  LONG_T;

        public static type UVPair = Pair[types.VERT_T, types.VERT_T];
        public static type UVWTriplet = Triplet[types.VERT_T, types.VERT_T, types.WEIGHT_T];
        public static type UVDSQuad= Quad[types.VERT_T, types.VERT_T, types.LONG_T, types.DOUBLE_T];

};
