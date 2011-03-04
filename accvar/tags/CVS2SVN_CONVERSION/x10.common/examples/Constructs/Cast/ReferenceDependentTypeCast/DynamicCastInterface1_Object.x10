/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

 /**
  * Purpose: Checks casting from an interface works correctly.
  * Note: The cast from interface to unconstrained class should not generate runtime checking
  *       as no constraints are specified in the cast.
  * @author vcave
  **/
public class DynamicCastInterface1_Object extends x10Test {

	public boolean run() {
		X10InterfaceOne x10Interface = 
			new X10DepTypeClassOne(0);
		
		X10DepTypeClassOne(:p==0) toClassConstraint = 
			(X10DepTypeClassOne(:p==0)) x10Interface;
		
		X10DepTypeClassOne toClassNoConstraint = 
			(X10DepTypeClassOne) x10Interface;
		
		return true;
	}

	public static void main(String[] args) {
		new DynamicCastInterface1_Object().execute();
	}

}
 