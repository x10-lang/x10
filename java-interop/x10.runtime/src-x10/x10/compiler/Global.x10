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

import x10.lang.annotations.ClassAnnotation;
import x10.lang.annotations.MethodAnnotation;

/**
/**
 * Global has two uses -- as an annotation for Classes and for Methods. These uses
 * can be understood in the context of the "Dual Class idiom" and "Single Class idiom" 
 * for implementing global objects. See x10.compiler.Pinned.
 * <p> The @Global class annotation is used to support the Dual Class idiom: it
 * marks the class whose instances are proxy objects. The @Global method annotation is used to 
 * support the Single Class idiom: it marks methods that are intended to be invoked on proxies.
 * 
 * <p>This method is not processed by any phase of the compiler at this time. 
 *  It is intended to document programmer intent.
 * 
 *  @see Pinned
 */
public interface Global extends MethodAnnotation, ClassAnnotation { }
