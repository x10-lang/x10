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

package x10.lang;
import x10.lang.annotations.MethodAnnotation;
import x10.lang.annotations.FieldAnnotation;
import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/**
 * Annotation intended to be used to mark flat asyncs, i.e. asyncs 
 * whose bodies do not further spawn asyncs.
 */
public interface flat
    extends ExpressionAnnotation, StatementAnnotation, MethodAnnotation {
}
