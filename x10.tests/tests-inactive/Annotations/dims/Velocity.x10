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

public interface Velocity extends Measure {
    @DerivedUnit(Length.m/Time.s) static mps: double = _;
    @DerivedUnit(Length.mile/Time.hour) static mph: double = _;
    @DerivedUnit(Length.km/Time.hour) static kph: double = _;
    @DerivedUnit(Length.foot/Time.s) static fps: double = _;
    @DerivedUnit(Length.nautical_mile/Time.hour) static knot: double = _;

    static c: double = 2.99792458E8;
}
