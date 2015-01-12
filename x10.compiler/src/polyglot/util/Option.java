/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;


public abstract class Option<T> {
    public abstract T get();

    public static final None<?> NONE = new None<Object>();
    @SuppressWarnings("unchecked") // Casting to a generic type
    public static <S> None<S> None() { return (None<S>) NONE; }
    public static <S> Some<S> Some(S s) { return new Some<S>(s); }
    
    public static class Some<T> extends Option<T> {
        T t;

        public Some(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }

        public String toString() { return "Some(" + t + ")"; }
    }

    public static class None<T> extends Option<T> {
        private None() {}

        public T get() {
            return null;
        }
        
        public String toString() { return "None"; }
    }
}