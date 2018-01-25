/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2018.
 */

public class ConsumerX10[T] implements java.util.function.Consumer/*[T]*/ {
    /*
     * HACK: X10 sees Java generic type as its erasure.
     * In order to override Java generic method in X10, we eliminate type parameters from X10 method parameters.
     * It changes X10 method signature, but it works in some cases because this is covariant override.
     * This is the limitation comes from current design.
     */
    public def accept(value:Any/*T*/):void {}
}
