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
 * This annotation on a method allows it to be called by dynamically dispatching during construction.
 * (See also @NonEscaping annotation.)
 *
 * <p>@NoThisAccess marks the fact that "this" cannot be used at all in the method.
 * The compiler checks @NoThisAccess as follows:
 * 1) @NoThisAccess must be preserved by overriding, i.e., when overriding a method annotated with
 *  @NoThisAccess then the overriding method must be annotated with @NoThisAccess.
 * 2) The method cannot access "this" or "super" at all (cannot escape "this", read, nor write from any field).
 *
 * @NoThisAccess is different from @NonEscaping as follows:
 * 1) @NoThisAccess methods cannot access "this" at all, whereas @NonEscaping can read and write to the fields of "this".
 * 2) @NoThisAccess methods can be called after the constructor call (either super(...) or this(...)),
 * whereas @NonEscaping methods can be called only after the property call (property(...)).
 * 3) @NoThisAccess can be overriden, whereas @NonEscaping methods must be private/final.
 *
 * <p>@NoThisAccess is not checked on native methods because they do not have a body.
 */
public interface NoThisAccess extends MethodAnnotation { }
