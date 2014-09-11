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

package x10.core;

import java.io.Serializable;

/**
 * Abstract implementation-level superclass of all X10 classes.
 */
@SuppressWarnings("serial")
public abstract class Ref implements Any, Serializable {
    
    // N.B. this is called implicitly by all subclasses of Ref
    public Ref() {}

    // constructor just for allocation
    public Ref(java.lang.System[] $dummy) {}

    // constructor for non-virtual call
    public final Ref x10$lang$Object$$init$S() {return this;}

    @Override
    public String toString() {
        return x10.lang.System.identityToString(this);
    }
 
// not used (same as Java)
//    @Override
//    public int hashCode() {
//        return x10.lang.System.identityHashCode$O(this);
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        return x10.lang.System.identityEquals$O(this, other);
//    }
}
