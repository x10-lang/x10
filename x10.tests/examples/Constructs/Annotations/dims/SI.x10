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

public interface SI {
    const yotta: double = 1e24, Y: double = 1e24;
    const zetta: double = 1e21, Z: double = 1e21;
    const exa: double = 1e18, E: double = 1e18;
    const peta: double = 1e15, P: double = 1e15;
    const tera: double = 1e12, T: double = 1e12;
    const giga: double = 1e9,  G: double = 1e9;
    const mega: double = 1e6,  M: double = 1e6;
    const kilo: double = 1e3,  k: double = 1e3;
    const hecto: double = 1e2, h: double = 1e2;
    const deca: double = 1e1,  da: double = 1e1;
    const deci: double = 1e-1, d: double = 1e-1;
    const centi: double = 1e-2, c: double = 1e-2;
    const milli: double = 1e-3, m: double = 1e-3;
    const micro: double = 1e-6, u: double = 1e-6;
    const nano: double = 1e-9,  n: double = 1e-9;
    const pico: double = 1e-12, p: double = 1e-12;
    const femto: double = 1e-15, f: double = 1e-15;
    const atto: double = 1e-18, a: double = 1e-18;
    const zepto: double = 1e-21, z: double = 1e-21;
    const yocto: double = 1e-24, y: double = 1e-24;

    @BaseUnit const meter: double = Measure._;
    @BaseUnit const kilogram: double = Measure._;
    @BaseUnit const second: double = Measure._;
    @BaseUnit const ampere: double = Measure._;
    @BaseUnit const kelvin: double = Measure._;
    @BaseUnit const mole: double = Measure._;
    @BaseUnit const candela: double = Measure._;

    @BaseUnit const radian: double = Measure._, rad : double = Measure._;
    @BaseUnit const steradian: double = Measure._, sr: double = Measure._;
    
    @DerivedUnit(1 / second) const hertz: double = Measure._;

    @DerivedUnit(meter * kilogram / (second*second)) const newton: double = Measure._;
    @DerivedUnit(newton * meter) const joule: double = Measure._;
    @DerivedUnit(joule / second) const watt: double = Measure._;
    @DerivedUnit(newton / (meter*meter)) const pascal: double = Measure._;
    @DerivedUnit(candela * steradian) const lumen: double = Measure._;
    @DerivedUnit(lumen / (meter*meter)) const lux: double = Measure._;
    @DerivedUnit(second * ampere) const coulomb: double = Measure._;
    @DerivedUnit(watt / ampere) const volt: double = Measure._;
    @DerivedUnit(volt / ampere) const ohm: double = Measure._;
    @DerivedUnit(coulomb / volt) const farad: double = Measure._;
    @DerivedUnit(joule / ampere) const weber: double = Measure._;
    @DerivedUnit(volt * second / (meter*meter)) const tesla: double = Measure._;
    @DerivedUnit(volt * second / ampere) const henry: double = Measure._;
    @DerivedUnit(1 / ohm) const siemens: double = Measure._;
    @DerivedUnit(1 / second) const becquerel: double = Measure._;
    @DerivedUnit(joule / kilogram) const gray: double = Measure._;
    @DerivedUnit(joule / kilogram) const sievert: double = Measure._;
    @DerivedUnit(mole / second) const katal: double = Measure._;

    @DerivedUnit(kelvin - 273.16) const celsius: double = Measure._;
}
