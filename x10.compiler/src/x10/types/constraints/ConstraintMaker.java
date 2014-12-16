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

import x10.constraint.XFailure;

/**
 * A ConstraintMaker is used in circumstances where a lazy or 
 * delayed evaluation of a constraint is appropriate. A typical situation is 
 * that the type checker needs to determine whether S is a subtype of T, given 
 * the current typing environment Gamma. Now we need to determine if c(S) (the 
 * constraint of S) entails c(T) (the constraint of T), given Gamma. So we 
 * encapsulate the constraint projection from Gamma into a ConstraintMaker, 
 * so that entailment can force evaluation only if it needs to (i.e. when it 
 * finds a basic constraint 
 * 
 * @author vj
 *
 */
public interface ConstraintMaker {

    /**
     * Produce the constraint.
     * @return
     * @throws XFailure
     */
    CConstraint make() throws XFailure;

}
