/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class X10DepTypeSubClassOne(int a) extends X10DepTypeClassOne {

	public X10DepTypeSubClassOne(int a,int q) {
	    super(a);
	    this.a=q;
	}
}

