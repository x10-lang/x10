/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A struct cannot have a rooted field.
 * @author vj
 */
public class StructCannotHaveRootedField_MustFailCompile extends x10Test {

    struct A {
	 rooted val x:int=5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotHaveRootedField_MustFailCompile().execute();
    }
}
