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

public interface Area extends Measure {
    @DerivedUnit(Length.cm * Length.cm) static cm2: double = _;
    @DerivedUnit(Length.m * Length.m) static m2: double = _;
    @DerivedUnit(100 * m2) static are: double = _;
    @DerivedUnit(Length.km * Length.km) static km2: double = _;

    @DerivedUnit(Length.inch * Length.inch) static inch2: double = _;
    @DerivedUnit(Length.foot * Length.foot) static foot2: double = _;
    @DerivedUnit(Length.mile * Length.mile) static mile2: double = _;

    @DerivedUnit(43560 * foot2) static acre: double = _;
    @DerivedUnit(acre * 100 / 40.5) static hectare: double = _;
    @DerivedUnit(Length.mile * Length.mile) static section: double = _;

    @DerivedUnit(40 * Length.rod * Length.rod) static rood: double = _;
}
