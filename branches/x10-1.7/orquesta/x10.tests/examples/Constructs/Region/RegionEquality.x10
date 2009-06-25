/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

public class RegionEquality extends x10Test {

	public def run(): boolean = {
		val size: int = 10;
		val R: region = [0..size-1, 0..size-1];
		return R==[0..size-1, 0..size-1];
	}
	public static def main(var args: Rail[String]): void = {
		 new RegionEquality().execute();
	}
}
