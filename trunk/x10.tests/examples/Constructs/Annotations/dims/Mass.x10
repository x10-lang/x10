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

public interface Mass extends Measure {
    @DerivedUnit(SI.kilogram) const kg: double = _;
    @DerivedUnit(1e-3 * kg) const g: double = _;
    @DerivedUnit(1e-3 * g) const mg: double = _;
    @DerivedUnit(1e-6 * g) const ug: double = _;
    @DerivedUnit(1e+3 * kg) const tonne: double = _;

    @DerivedUnit(kg / 2.2046) const lb: double = _;
    @DerivedUnit(2000 * lb) const ton: double = _;
    @DerivedUnit(2240 * lb) const long_ton: double = _;
    @DerivedUnit(lb / 16) const troy_ounce: double = _, ounce: double = _, oz: double = _;
    @DerivedUnit(oz / 7000) const grain: double = _;
    @DerivedUnit(oz * 12) const troy_pound: double = _;
    @DerivedUnit(20 * grain) const scruple: double = _;
    @DerivedUnit(3 * scruple) const dram: double = _;
    @DerivedUnit(8 * dram) const apothecary_ounce: double = _;
    @DerivedUnit(12 * apothecary_ounce) const apothecary_pound: double = _;
    @DerivedUnit(24 * grain) const pennyweight: double = _;
    @DerivedUnit(14.594 * kg) const slug: double = _;
}
