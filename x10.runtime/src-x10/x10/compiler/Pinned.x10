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

import x10.lang.annotations.ClassAnnotation;
import x10.lang.annotations.MethodAnnotation;

/**
 * Pinned has two uses -- as an annotation for Classes and for
 * Methods. These uses can be understood in the context of the <em>Dual
 * Class idiom</em> and <em>Single Class idiom</em> for implementing global
 * objects.
 * 
 * <p> <bf>Dual class idiom</bf>: This idiom uses two classes, an
 * <code>@Pinned</code> class <code>P</code> that captures the mutable
 * state of the object (an instance of <code>P</code> is called a <em>
 * root object</em> and there is one such object for each global
 * object), and a <code>@Global</code> class <code>GlobalP</code> with
 * a field <code>val root:GlobalRef[P]</code> (containing a global
 * reference to the root object). Instances of <code>P</code> are not
 * intended to cross place boundaries (hence they are
 * "pinned"). Therefore fields of <code>P</code> need not be marked
 * transient. Methods on <code>P</code> are implicitly considered
 * <code>@Pinned</code>. They can directly operate on the mutable
 * state of the global object by operating on the current object.
 * Instances of <code>GlobalP</code> are intended to cross
 * place-boundaries (and are called <em>proxy objects</em> -- there
 * may be zero or more at each place). Typically <code>GlobalP</code>
 * will have (non-transient) immutable fields containing references to
 * instances of global classes; this state is serialized when a
 * <code>GlobalP</code> instance crosses place
 * boundaries. <code>GlobalP</code> methods are implicitly considered
 * <code>@Global</code> and may use <code>at</code> operations to
 * place-shift to <code>root.home</code> to operate on the
 * <code>root()</code> object.  The <code>equals()</code> method on
 * <code>GlobalP</code> objects should return <code>true</code> only
 * if the <code>root</code> fields are equal.
 * 
 * 
 * <p> <bf>Single Class idiom</bf>: This idiom was used in X10 2.0 The
 * same class <code>C</code> is used to implement both the root object and the
 * proxy object.  Hence a global object is implemented by one or more
 * instances of this class. One instance is the root object and the
 * others are proxies. The class should have a field <code>val 
 * root:GlobalRef[C]</code> initialized with a global reference to
 * itself. The equals() method on C should return true only if the
 * root fields are equal.  Fields marked "transient" contain
 * meaningful state only in the root object and should be ignored in
 * the proxy objects. Methods marked @Pinned are intended to be
 * executed only on the root object; they should have a guard
 * {here==root.home} and can directly access transient fields of the
 * root object using "this".  Methods marked @Global are intended to
 * be executed on root or proxy objects and typically do not have a
 * place guard. They should not directly access any transient field,
 * but should place-shift to root.home.  At root.home they can access
 * the root object by evaluating root(), and may access its transient
 * fields.
 * 
 * <p> Note that one can move from the Single Class Idiom to the Dual
 * Class Idiom (highy recommended)
 * <p> The @Pinned class annotation is used to support the Dual Class
 * idiom: it marks the class whose instances are root objects. If a
 * field is not transient, it should not have a type that permits an
 * instance of a pinned class being stored in that field.
 * <p> The @Pinned method annotation is used to support the
 * Single Class idiom: it marks methods that are intended to be
 * invoked on the root object.
 * 
 * <p> It may make sense for the runtime to dynamically throw errors
 * if an instance of an @Pinned class is detected crossing place
 * boundaries.
 * 
 * <p>This method is not processed by any phase of the compiler at
 *  this time.  It is intended to document programmer intent.
 *
 * @see Global
 * 
 */
public interface Pinned extends MethodAnnotation, ClassAnnotation { }
