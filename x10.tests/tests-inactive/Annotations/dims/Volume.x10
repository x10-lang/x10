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

public interface Volume extends Measure {
    @DerivedUnit(Length.cm * Length.cm * Length.cm) static cc: double = _;
    @DerivedUnit(cc) static ml: double = _;
    @DerivedUnit(1000 * ml) static l: double = _;

    @DerivedUnit(Length.inch * Length.inch * Length.inch) static cu_inch: double = _;
    @DerivedUnit(Length.foot * Length.foot * Length.foot) static cu_foot: double = _;

    @DerivedUnit(128 * cu_foot) static cord: double = _;
    @DerivedUnit(42 * gallon) static barrel: double = _;

    @DerivedUnit(tbsp / 3) static tsp: double = _;
    @DerivedUnit(oz / 2) static tbsp: double = _;
    @DerivedUnit(cup / 8) static oz: double = _;
    @DerivedUnit(pint / 2) static cup: double = _;
    @DerivedUnit(quart / 2) static pint: double = _;
    @DerivedUnit(gallon / 4) static quart: double = _;
    @DerivedUnit(gallon / 2) static half_gallon: double = _;
    @DerivedUnit(3.785 * l) static gallon: double = _;
    @DerivedUnit(4 * oz) static gill: double = _;
}
