/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author pvarma
 */
public class Prop1(i:int, j:int){i == j} extends x10Test {

	public def this(k:int):Prop1 = {
	    property(k,k);
	}
	public def run()=true;
	
	public static def main(a:Rail[String]):void = {
		new Prop1(2).execute();
	}
}
