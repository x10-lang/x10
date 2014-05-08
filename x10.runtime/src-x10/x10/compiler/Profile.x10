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

package x10.compiler;

import x10.lang.annotations.*;

/**
 * This annotation on an at statement makes profiling information relating to
 * the statement available to applications.
 *
 * EXAMPLE:
 *
 *      val x = new Runtime.Profile();
 *      @Profile(x) at (here.next()) {
 *      }
 *      // x now populated with profiling data
 *
 * @see x10.lang.Runtime.Profile
 */
public interface Profile(prof:Runtime.Profile) extends StatementAnnotation, ExpressionAnnotation {
}
