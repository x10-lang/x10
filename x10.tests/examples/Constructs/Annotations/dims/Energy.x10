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

public interface Energy extends Measure {
    @DerivedUnit(SI.joule) const joule: double = _, J: double = _;
    @DerivedUnit(Mass.g * Area.cm2 / (Time.s*Time.s)) const erg: double = _;

    @DerivedUnit(1.602e-19 * J) const eV: double = _;

    @DerivedUnit(1055.06 * J) const Btu: double = _;
    @DerivedUnit(Force.lbf * Length.foot) const ft_lbf: double = _;
    @DerivedUnit(Power.kW * Time.hour) const kWh: double = _;
    
    @DerivedUnit(4.186 * J) const calorie: double = _, cal: double = _;
    @DerivedUnit(1000 * cal) const Calorie: double = _, kcal: double = _;
}
