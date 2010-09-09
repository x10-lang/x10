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

package dims;

public interface Time extends Measure {
    @DerivedUnit(SI.second) const s: double = _;
    @DerivedUnit(s / 1000) const ms: double = _;
    @DerivedUnit(s * 60) const minute: double = _, min: double = _;
    @DerivedUnit(min * 60) const hr: double = _, hour: double = _;
    @DerivedUnit(hour * 24) const day: double = _;
    @DerivedUnit(day * 7) const week: double = _;
    @DerivedUnit(2 * week) const fortnight: double = _;
    @DerivedUnit(day * 365.25) const julian_year: double = _, year: double = _, a: double = _;
    @DerivedUnit(day * 365.2425) const gregorian_year: double = _;
    @DerivedUnit(10 * year) const decade: double = _;
    @DerivedUnit(100 * year) const century: double = _;
    @DerivedUnit(1000 * year) const millenium: double = _;
    @DerivedUnit(1e6 * year) const megayear: double = _, Ma: double = _;
    @DerivedUnit(1e9 * year) const gigayear: double = _, Ga: double = _;
}
