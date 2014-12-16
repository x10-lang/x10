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

package dims;

public interface Current extends Measure {
    @DerivedUnit(SI.ampere) static ampere: double = _, amp: double = _, A: double = _;
    @DerivedUnit(1e-3 * A) static milliamp: double = _, mA: double = _;
}
