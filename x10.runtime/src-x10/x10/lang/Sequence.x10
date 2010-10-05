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
 * A Sequence permits random access (through the apply method), can be used in a
 * structured for loop for (t:T in seq) ... and has a size.
 */
public interface Sequence[+T](size:int) extends (int)=> T, Iterable[T] {}
