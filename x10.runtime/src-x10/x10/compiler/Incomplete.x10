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

import x10.lang.annotations.MethodAnnotation;

/**
 * This annotation is used to mark a method as incomplete.
 * The body of the method should contain something like throw new UnimplementedException().

 * <p> This annotation is putrely for documentation.
 */
public interface Incomplete extends MethodAnnotation { }
