/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A field cannot have a proto type.
 * @author vj
 */
public class ProtoField_MustFailCompile extends x10Test {

	class A{}
   var x: proto A;
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoField_MustFailCompile().execute();
    }
}
