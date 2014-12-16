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

/**
 * This annotation is used to allow the programmer
 * to direct the compiler not to insert a super
 * constructor invocation in annotated constructor.
 * This annotation is intended to be used internally
 * by the compiler. It should not be used otherwise.</p>
 *
 * This annotation is processed by the X10 compiler's
 * FieldInitializerMover visitor.
 */
public interface NoSuperCall extends MethodAnnotation { }
