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

package x10.lang;

/**
 * Base calss for Accumulator[T] that has all the methods but without the type parameter T.
 * Instead we perform casts at runtime.
 * We use this class because we do not have existentials in X10 (e.g., when we do not know T such as in ArrayList[Acc].
 */
public abstract class Acc {
    public abstract def supply(t:Any):void;
    public abstract def reset(t:Any):void;
    public abstract def result():Any;

    // These methods are called by the Runtime. Do not use them.
    public abstract def calcResult():Any;
    public abstract def acceptResult(a:Any):void;
    public abstract def getRoot():GlobalRef[Acc];
    public abstract def home():Place;
}