/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing arrays of future<T>.
 *
 * @author kemal, 5/2005
 */
public class ArrayFuture extends x10Test {

	public def run(): boolean = {
		final val d: dist = Dist.makeConstant([1..10, 1..10], here);
		final val ia: Array[future<int>] = new Array[future<int>](d, (var point [i,j]: point): future<int> => { return future(here){i+j}; });
		for (val (i,j): point in ia) chk(ia(i, j).force() == i+j);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayFuture().execute();
	}
}
