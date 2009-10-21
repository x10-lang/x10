/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A struct cannot subclass a class.
 * @author vj
 */
public class StructCannotSubclassClass_MustFailCompile extends x10Test {

	class B {}
    struct A extends B {
	 val x:int=5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotSubclassClass_MustFailCompile().execute();
    }
}
