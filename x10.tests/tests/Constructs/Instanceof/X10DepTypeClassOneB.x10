/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

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
public class X10DepTypeClassOneB(p:long) implements X10InterfaceOneB {

	public def this(p:long) = {
	    property(p);
	}

	public  def interfaceMethod():void  = {}

}
