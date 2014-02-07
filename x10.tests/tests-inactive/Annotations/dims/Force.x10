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

public interface Force extends Measure {
    @DerivedUnit(SI.newton) static newton: double = _, N: double = _;
    @DerivedUnit(32.1740485 * Mass.lb * Length.foot / (Time.s*Time.s)) static lbf: double = _;
    @DerivedUnit(Mass.g * Length.cm / (Time.s*Time.s)) static dyn: double = _;
}
