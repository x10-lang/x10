/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types.constraints;

import x10.constraint.XVar;

public interface ThisVar {

    /**
     * We distinguish the variable "this" since the compiler needs to perform
     * special actions for this. So each constraint keeps track of its this
     * variable. This is typically set using setThisVar when a substitution
     * is being applied. 
     * @return
     */
    XVar thisVar();

}
