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

package x10.types.constraints;

import polyglot.types.Context;
import polyglot.types.Type;
import x10.constraint.XFailure;
import x10.constraint.XVar;

/**
 * @author Louis Mandel
 * @author Olivier Tardieu
 *
 */
public interface CRequirement {

	/**
	 * The context in which the requirement must be satisfied.
	 */
	public CConstraint hypothesis();

	/**
	 * The requirement to satisfy.
	 */
	public CConstraint require();

}
