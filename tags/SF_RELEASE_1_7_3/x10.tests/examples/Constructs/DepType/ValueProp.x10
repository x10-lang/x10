/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted for value classes.
 *
 * @author vj
 */
public class ValueProp extends x10Test {
    static value Value1(i:int, j:int) {
	  public def this(i:int, j:int): Value1{self.i==i,self.j==j}=  {
	    property(i,j);
	  }
	}
	public def run():boolean= {
	new Value1(2,3);
	return true;
	}
	 
	public static def main(var args: Rail[String]): void = {
		new ValueProp().execute();
	}
	
}
