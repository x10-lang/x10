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

import x10.compiler.Native;

/**
 * A Sequence represents a fixed-size ordered collection of entities of type T
 * that can be accessed randomly and iterated over using an enhanced-for loop,
 * i.e. for (t:T in S) ...
 * 
 * <p> Sequence provides no operations to update the objects in the underlying collection. This
 * does not mean that the entities in the collection cannot be replaced by others -- just that if that
 * happens, it happens through some other (non-Sequence) reference to underlying implementation objects. 
 * It is up to the implementer of the class or interface implementing Sequence to provide any additional 
 * guarantees.
 * 
 * @author vj 10/10
 */
public interface Sequence[T] extends (Int)=> T, Iterable[T] {
    @Native("cuda", "(#0).raw[#1]")
    public operator this(Int) : T;

    public property size():int;
}
