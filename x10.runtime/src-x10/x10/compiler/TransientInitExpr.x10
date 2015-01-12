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

package x10.compiler;

import x10.lang.annotations.FieldAnnotation;

/**
 * This annotation indicates that when a transient field
 * is deserialized, it should be initialized with the result
 * of the argument expression.  The expression will be evaluated
 * after all non-transient fields are deserialized and initialized.
 *
 * TODO: This really should be 
 *    TransientInitExpr[T](init:T)
 * but we can't say that in X10 2.4.
 */
public interface TransientInitExpr(init:Any) extends SuppressTransientError { }

