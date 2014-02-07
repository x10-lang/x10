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

public interface Time extends Measure {
    @DerivedUnit(SI.second) static s: double = _;
    @DerivedUnit(s / 1000) static ms: double = _;
    @DerivedUnit(s * 60) static minute: double = _, min: double = _;
    @DerivedUnit(min * 60) static hr: double = _, hour: double = _;
    @DerivedUnit(hour * 24) static day: double = _;
    @DerivedUnit(day * 7) static week: double = _;
    @DerivedUnit(2 * week) static fortnight: double = _;
    @DerivedUnit(day * 365.25) static julian_year: double = _, year: double = _, a: double = _;
    @DerivedUnit(day * 365.2425) static gregorian_year: double = _;
    @DerivedUnit(10 * year) static decade: double = _;
    @DerivedUnit(100 * year) static century: double = _;
    @DerivedUnit(1000 * year) static millenium: double = _;
    @DerivedUnit(1e6 * year) static megayear: double = _, Ma: double = _;
    @DerivedUnit(1e9 * year) static gigayear: double = _, Ga: double = _;
}
