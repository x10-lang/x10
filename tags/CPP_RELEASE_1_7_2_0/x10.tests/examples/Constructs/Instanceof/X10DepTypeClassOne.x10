/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassOne(p:int) implements X10InterfaceOne {

	
	public def this(p:int) = {
	    this.p=p;
	}

	public  def interfaceMethod():void  = {}

}
