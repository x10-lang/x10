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

package x10.types;

import polyglot.types.LocalDef;
import x10.constraint.XTerm;

public interface X10LocalDef extends LocalDef, X10Def {

    void setUnnamed();
    boolean isUnnamed();
    void setAsyncInit();
    boolean isAsyncInit();
	XTerm placeTerm();
	void setPlaceTerm(XTerm xt);
}
