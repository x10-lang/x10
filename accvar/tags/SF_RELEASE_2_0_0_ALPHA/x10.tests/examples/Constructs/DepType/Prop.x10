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
 * @author vj
 */
public class Prop(i: int,  j: int) extends x10Test  {

	public def this(i: int, j: int): Prop{self.i==i,self.j==j} = {
	    property(i,j);
	}
	public def run(): boolean = true;
	public static def main(var args: Rail[String]): void = {
		new Prop(2,3).execute();
	}
	}
