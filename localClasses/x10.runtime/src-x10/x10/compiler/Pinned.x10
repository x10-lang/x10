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

/**
 * Pinned has two uses -- as an annotation for Classes and for Methods. These uses
 * can be understood in the context of the "Dual Class idiom" and "Single Class idiom" 
 * for implementing global objects.
 * 
 * <p> Dual class idiom: This idiom uses two classes, an @Pinned class P that captures
 * the mutable state of the object (an instance of P is called a root object and there is one such object 
 * for each global object), and a @Global class GlobalP with a val field root:GlobalRef[P]
 * (containing a global reference to the root object). Instances of P are not intended to cross place boundaries 
 * (hence they are "pinned"). Thus fields of P are not marked transient. Methods on P are implicitly
 * considered @Pinned -- they can directly operate on the mutable state of the global object without 
 * crossing place-boundaries.  Instances of GlobalP are intended to cross place-boundaries 
 * (and are called proxy objects -- there may be zero or more at each place). Typically GlobalP will have 
 * (non-transient) immutable fields containing references to instances of global classes; this state 
 * is serialized when a GlobalP instance crosses place boundaries. GlobalP methods are implicitly marked 
 * @Global and may use at operations to place-shift to root.home to operate on the root object. 
 * The equals() mehod on GlobalP objects should return true only if the root fields are equal.
 * 
 * 
 * <p> Single Class idiom: This idiom is deprecated in favor of the Dual Class idiom. (This was used in X10 2.0.)
 * The same class C is used to implement both the root object and the proxy object. 
 * Hence a global object is implemented by one or more instances of this class. One instance
 * is the root object and the others are proxies. The class should have a val field root:GlobalRef[C] initialized
 * with a global reference to itself. The equals() method on C should return true only if the root fields are equal.
 * Fields marked "transient" contain meaningful state only in the root object and should be ignored in the 
 * proxy objects. Methods marked @Pinned are intended to be executed only on the root object; they should 
 * have a guard {here==root.home} and can directly access transient fields of the root object using "this". 
 * Methods marked @Global are intended to be executed on root or proxy objects and typically do not have a 
 * place guard. They should not directly access any transient field, but should place-shift to root.home. 
 * At root.home they can access the root object by evaluating root(), and may access its transient fields. 
 * 

 * <p> The @Pinned class annotation is used to support the Dual Class idiom: it
 * marks the class whose instances are root objects. The @Pinned method annotation is used to 
 * support the Single Class idiom: it marks methods that are intended to be invoked on the root object.
 * 
 * <p> It may make sense for the runtime to dynamically throw errors if an instance of an @Pinned class
 * is detected crossing place boundaries.
 * 
 * <p>This method is not processed by any phase of the compiler at this time. 
 *  It is intended to document programmer intent.
 * @see Global
 * 
 */
public interface Pinned extends MethodAnnotation, ClassAnnotation { }
