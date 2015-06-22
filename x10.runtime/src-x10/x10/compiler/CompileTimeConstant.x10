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
 * Annotation to allow the compiler front-end access to command-line constants.
 * 
 * option: the name of a static field in x10.Configuration, possibly prepended by "!".
 * 
 * The Inliner will replace a call to such a method with a Lit node representing the value of the option
 * (negated, if the name is preceded by "!" and the type is boolean or Boolean).
 */
public interface CompileTimeConstant(option: String) extends MethodAnnotation { }
