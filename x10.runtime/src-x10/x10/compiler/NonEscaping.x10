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

package x10.compiler;

import x10.lang.annotations.MethodAnnotation;

/**
 * NonEscaping is an annotation that can be used on methods for two purposes:
 * 1) for calling a super method during construction, and
 * 2) for doing dynamic dispatching during construction.
 *
 * Object construction (or initialization) in X10 is more strict than Java:
 * X10 has restricting rules that prevent seeing the default value of val fields
 * (as opposed to final fields in Java, because it is possible to see the default value of final fields in Java)
 * You can read the value of a val field only after it is definitely been assigned.
 *
 *
 * <p>@NonEscaping marks the fact that "this" does not escape (also called leak) from the method.
 * A method is NonEscaping if:
 * 1) the method is either final, private or annotated with @NonEscaping.
 * 2) in the method body, "this" is only used in field access, field assignment,
 *    and as the reciever of NonEscaping methods.
 * 2) in the method body, "super" is only used in field access, field assignment,
 *    and as the reciever of methods annotated with @NonEscaping.
 *
 * The compiler checks @NonEscaping as follows:
 * 1) @NonEscaping must be preserved by overriding, i.e., when overriding a method annotated with
 *  @NonEscaping(readFromFields) then the overriding method must be annotated with exactly the same annotation.
 * 2) The fields in readFromFields must be declared in the class (and cannot include fields of the superclass).
 * 3) The method can read only from fields in readFromFields.
 *  NonEscaping is of course not checked on native methods because they do not have a body.
 *
 * All constructors and field initializers in X10 must be NonEscaping.
 *
 * <p>It is considered good practice to mark all methods called from a constructor as @NonEscaping.
 * 
 */
public interface NonEscaping(readFromFields:String) extends MethodAnnotation { }
