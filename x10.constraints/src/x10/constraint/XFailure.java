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

package x10.constraint;


/**
 * A representation of failure. Thrown when the constraint becomes inconsistent.
 * @author vj
 *
 */
public class XFailure extends Exception {
    private static final long serialVersionUID = 8913293615033249280L;
    Object etc;
	public XFailure() {super();}
	public XFailure(Throwable cause) {super(cause);}
	public XFailure(Object etc) {this.etc = etc;}
	public XFailure(String m) {super(m);}
	public XFailure(String m, Throwable cause) {super(m, cause);}
	public XFailure(String m, Object etc) {super(m);this.etc = etc;}
}
