package ssca2;

import x10.util.*;

public class Types {
	static type INT_T =  Int;
	static type DOUBLE_T = double;
	static type LONG_T = Int; //x10.lang.Long; /* 64 bit */
	static type WEIGHT_T = LONG_T;
	static type VERT_T =  LONG_T;
	public static type EDGE_T = Triplet[VERT_T, VERT_T, WEIGHT_T];
	
	public static type UVPair = Pair[VERT_T, VERT_T];
	
};
