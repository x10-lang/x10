/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cannot return a value of type boolean from a method whose return type is boolean(:self==true).
 *
 * @author vj
 */
public class DepTypeInMethodRet_MustFailCompile extends x10Test {
    
   public def m(var t: boolean): boolean(true) = t;
	public def run()=m(false);
	public static def main(var args: Rail[String]): void = {
		new DepTypeInMethodRet_MustFailCompile().execute();
	}
}
