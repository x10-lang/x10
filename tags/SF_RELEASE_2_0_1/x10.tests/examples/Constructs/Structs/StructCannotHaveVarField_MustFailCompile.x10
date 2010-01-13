/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A struct cannot have a var field.
 * @author vj
 */
public class StructCannotHaveVarField_MustFailCompile extends x10Test {

    struct A {
	 var x:int=5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotHaveVarField_MustFailCompile().execute();
    }
}
