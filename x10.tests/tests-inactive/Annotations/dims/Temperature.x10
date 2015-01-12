/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package dims;

public interface Temperature extends Measure {
    @DerivedUnit(SI.kelvin) static kelvin: double = _, K: double = _;
    @DerivedUnit(SI.celsius) static celsius: double = _, C: double = _;
    @DerivedUnit(C * 9. / 5. + 32.) static fahrenheit: double = _, F: double = _;
}
