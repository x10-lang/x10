/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Region r occuring in a distribution context is converted to r->here
 */
public class ArrayOverRegion extends x10Test {

	public def run(): boolean = {
		var r: region = [1..10, 1..10];
		var ia: Array[double] = 
		Array.make[double](r->here, (var (i,j): point)=> i+j to Double);
		chk(ia.dist.equals(Dist.makeConstant(r, here)));

		for (val p(i,j): point in r) chk(ia(p) == i+j);

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayOverRegion().execute();
	}
}
