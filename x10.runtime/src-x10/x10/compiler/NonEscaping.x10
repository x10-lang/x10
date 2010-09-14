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
 * NonEscaping is an annotation that can be used on final or private methods.
 *
 * <p>It marks the fact that "this" does not escape (also called leak) from the method.
 * A method is explicitly-NonEscaping if it is annotated with @NonEscaping and it is implicitly-NonEscaping.
 * A method is implicitly-NonEscaping if:
 * 1) "this" is only used in field access, field assignment,
 *    and as the reciever of implicitly-NonEscaping method invocation.
 * 2) "super" is only used in field access, field assignment,
 *    and as the reciever of explicitly-NonEscaping method invocation.
 * All constructors and field initializers in X10 must be implicitly-NonEscaping.
 *
 * <p>It is considered good practice to mark all methods called from a constructor as NonEscaping.
 * 
 * <p>NonEscaping is not checked on native methods.
 */
public interface NonEscaping extends MethodAnnotation { }
