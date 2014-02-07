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

public interface Mass extends Measure {
    @DerivedUnit(SI.kilogram) static kg: double = _;
    @DerivedUnit(1e-3 * kg) static g: double = _;
    @DerivedUnit(1e-3 * g) static mg: double = _;
    @DerivedUnit(1e-6 * g) static ug: double = _;
    @DerivedUnit(1e+3 * kg) static tonne: double = _;

    @DerivedUnit(kg / 2.2046) static lb: double = _;
    @DerivedUnit(2000 * lb) static ton: double = _;
    @DerivedUnit(2240 * lb) static long_ton: double = _;
    @DerivedUnit(lb / 16) static troy_ounce: double = _, ounce: double = _, oz: double = _;
    @DerivedUnit(oz / 7000) static grain: double = _;
    @DerivedUnit(oz * 12) static troy_pound: double = _;
    @DerivedUnit(20 * grain) static scruple: double = _;
    @DerivedUnit(3 * scruple) static dram: double = _;
    @DerivedUnit(8 * dram) static apothecary_ounce: double = _;
    @DerivedUnit(12 * apothecary_ounce) static apothecary_pound: double = _;
    @DerivedUnit(24 * grain) static pennyweight: double = _;
    @DerivedUnit(14.594 * kg) static slug: double = _;
}
