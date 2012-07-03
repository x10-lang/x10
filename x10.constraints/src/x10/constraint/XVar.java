/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;

public interface XVar extends XTerm {
	/**
	 * Returns the variables present in this variable. For nested field dereferences such as
	 *  a.f.g return an array consisting of all intermediate dereferences: [a, a.f, a.f.g].
	 *  The order matters, code in x10.compiler.x10cpp.visit.Emitter depends on this. 
	 * @return
	 */
	public XVar[] vars(); 
}

