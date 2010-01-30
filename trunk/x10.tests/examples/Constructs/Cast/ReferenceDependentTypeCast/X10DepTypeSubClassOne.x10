/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class X10DepTypeSubClassOne(int k) extends X10DepTypeClassOne {

	public X10DepTypeSubClassOne(:self.p==a&& self.k==q)(final int a, final int q) {
	    super(a);
	    property(q);
	}
}
