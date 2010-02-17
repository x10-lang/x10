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

package x10.types;

import java.io.Serializable;

import polyglot.types.Type;
import polyglot.util.Copy;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public interface SubtypeConstraint extends Copy, Serializable {

	static int SUBTYPE_KIND=0; // <:
	static int EQUAL_KIND =1;  // ==
	
	
    boolean isEqualityConstraint();
    boolean isSubtypeConstraint();
    boolean isKind(int kind);

    Type subtype();
    Type supertype();

    SubtypeConstraint subst(XTerm y, XRoot x);

}