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

package x10.constraint;


/**
 * A representation of logical variables. 
 * 
 * <p> EQVs  are existentially quantified
 * in each constraint in which they occur.

  * <p> UQVs  are free in the constraint and may occur in multiple constraints. 
 * @author vj
 * @see XUQV
 *
 */
public interface XEQV extends XVar {
}
