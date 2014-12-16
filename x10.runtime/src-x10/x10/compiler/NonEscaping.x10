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

import x10.lang.annotations.MethodAnnotation;
import x10.lang.annotations.FieldAnnotation;

/**
 * NonEscaping is an annotation that can be used on methods to mark the fact the method
 * does not escape "this" nor "super" during construction.
 * (See also @NoThisAccess annotation.)
 *
 * <p>All constructors and field initializers are implicitly NonEscaping.
 *
 * <p>It is considered good practice to mark all methods called from a constructor as @NonEscaping.
 *
 * Object construction (or initialization) in X10 is more strict than Java:
 * X10 has restricting rules that prevent seeing the default value of val fields
 * (as opposed to final fields in Java, because it is possible to see the default value of final fields in Java)
 * You can read the value of a val field only after it is definitely been assigned.
 *
 * <p>@NonEscaping marks the fact that "this" does not escape (also called leak) from the method.
 * A method is NonEscaping if:
 * 1) the method is either final, private, or the entire class is final.
 * 2) in the method body, "this" and "super" are only used in field access, field assignment,
 *    and as the reciever of NonEscaping methods.
 *    Calling a superclass method is legal only of the method is explicitly NonEscaping.
 *
 * <p>@NonEscaping is not checked on native methods because they do not have a body.
 */
public interface NonEscaping extends MethodAnnotation,FieldAnnotation { }
