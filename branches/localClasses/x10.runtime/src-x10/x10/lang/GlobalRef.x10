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

package x10.lang;


/**
 *
 * Create a global reference encapsulating a given object.  The ref has
 * the property home specifying the place at which it was
 * created. Besides that, the ref offers only the operations of Any at a
 * place other than the one where it was created (its home place).  Two
 * such refs are == if and only if they were created at the same place
 * and at that place the objects they encapsulate are ==.  
 *
 * <p> At its home place, the value when applied to the empty list of
 * arguments returns its encapsulated value.
 */
public struct GlobalRef[T](home:Place) { 

   /** 
    * Create a value encapsulating the given object of type T.
    */
   native public this(t:T); 

   /** 
    * Can only be invoked at the place at which the value was
    * created. Returns the object encapsulated in the value.
    */
   native def apply(){here == this.home}:T; 
}
