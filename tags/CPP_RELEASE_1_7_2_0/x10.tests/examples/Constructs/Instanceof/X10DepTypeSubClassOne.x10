/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class X10DepTypeSubClassOne(a:int) extends X10DepTypeClassOne {

	public def this(a:int, q:int) = {
	    super(a);
	    this.a=q;
	}
}
