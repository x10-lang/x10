/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that if D is of type Dist(R), and E of type Dist(R2), and R and R2
   have the same rank, then D and E have the same rank.
   @author vj 09/2008
 */
public class PropertyPropagationTest2 extends x10Test {
    def nothing()={}
	public def run(): boolean = {
	    val P  = Dist.makeUnique();
		val R1  = [0..1, 0..7] as Region; // horizontal strip
		val R2  = [4..5, 0..7] as Region; // horizontal strip
		val R3 = [0..7, 4..5] as Region; // vertical strip
		val R1orR2 = (R1 || R2);
		val R1orR2andR3  = R1orR2 && R3;
		val R1orR2orR3  = R1 || R2 || R3;
		val R1orR2minusR3 = R1orR2 - R3;
		val DR1orR2orR3  = Dist.makeCyclic(R1orR2orR3);
		var placeNum: int = 0;
		var offsetWithinPlace: int = 0;
		val np: int = Place.MAX_PLACES;
		//Check range restriction to a place
		for (val (k): Point in 0..np-1) {
			val DR1orR2orR3Here  = DR1orR2orR3 | P(k);
			for (val (i,j): Point(2) in DR1orR2orR3) {
			  nothing();
			}
		}

		//DR1orR2andR3 is restriction of DR1orR2orR3 to (R1||R2)&&R3
		val DR1orR2andR3 = DR1orR2orR3 | R1orR2andR3;
		//DR1orR2minusR3 is restr. of DR1orR2orR3 to (R1||R2)-R3
		val DR1orR2minusR3  = DR1orR2orR3 | R1orR2minusR3;
		val TD1 = DR1orR2orR3 - DR1orR2minusR3;
		val DR3  = DR1orR2orR3 | R3;
		//chk(TD1.equals(DR3));

		val TD2  = DR1orR2minusR3 && DR1orR2orR3;
		chk(TD2.equals(DR1orR2minusR3));

		// testing overlay with common mapping on common points
		val DR1orR2 = DR1orR2orR3 | R1orR2;
		val TD3  = (DR1orR2.overlay(DR3));
		chk(TD3.equals(DR1orR2orR3));

		//disjoint union
		val TD4  = DR1orR2andR3 || DR1orR2minusR3;
		chk(TD4.equals(DR1orR2));

		val TD9  = Dist.makeConstant(R1orR2andR3, P(0));
		val Doverlay =  (DR1orR2orR3.overlay(TD9));
		for (val (i,j): Point in Doverlay) {
			if (R1orR2andR3.contains([i, j] as Point)) {
				chk(Doverlay(i, j) == P(0) && TD9(i, j) == P(0));
			} else {
				chk(Doverlay(i, j) == DR1orR2orR3(i, j));
			}
		}

		val Dintersect = DR1orR2orR3 && Doverlay;
		for (val (i,j): Point(2) in ([0..7, 0..7] as Region)) {
			nothing();
		}
	    return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PropertyPropagationTest2().execute();
	}


}
