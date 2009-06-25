/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodArg_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
       public def this(i:int,j:int):Test{self.i==i&&self.j==j} = { 
	   property(i,j);
       }
    }
    public def m(t: Test{i==j}) = true; 

    public def run(): boolean = {
	// should fail because the declared type of the variable is just Test.
	val x: Test = new Test(1,1); 
	return m(x);
    }
    public static def main(var args: Rail[String]): void = {
	new DepTypeInMethodArg_MustFailCompile().execute();
    }
}
