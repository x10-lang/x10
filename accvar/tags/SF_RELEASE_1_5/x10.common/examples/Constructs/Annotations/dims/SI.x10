package dims;

public interface SI {
    double yotta = 1e24,  Y = 1e24;
    double zetta = 1e21,  Z = 1e21;
    double exa   = 1e18,  E = 1e18;
    double peta  = 1e15,  P = 1e15;
    double tera  = 1e12,  T = 1e12;
    double giga  = 1e9,   G = 1e9;
    double mega  = 1e6,   M = 1e6;
    double kilo  = 1e3,   k = 1e3;
    double hecto = 1e2,   h = 1e2;
    double deca  = 1e1,  da = 1e1;
    double deci  = 1e-1,  d = 1e-1;
    double centi = 1e-2,  c = 1e-2;
    double milli = 1e-3,  m = 1e-3;
    double micro = 1e-6,  u = 1e-6;
    double nano  = 1e-9,  n = 1e-9;
    double pico  = 1e-12, p = 1e-12;
    double femto = 1e-15, f = 1e-15;
    double atto  = 1e-18, a = 1e-18;
    double zepto = 1e-21, z = 1e-21;
    double yocto = 1e-24, y = 1e-24;

    @BaseUnit double meter = Measure._;
    @BaseUnit double kilogram = Measure._;
    @BaseUnit double second = Measure._;
    @BaseUnit double ampere = Measure._;
    @BaseUnit double kelvin = Measure._;
    @BaseUnit double mole = Measure._;
    @BaseUnit double candela = Measure._;

    @BaseUnit double radian = Measure._, rad = Measure._;
    @BaseUnit double steradian = Measure._, sr = Measure._;
    
    @DerivedUnit(1 / second) double hertz = Measure._;

    @DerivedUnit(meter * kilogram / (second*second)) double newton = Measure._;
    @DerivedUnit(newton * meter) double joule = Measure._;
    @DerivedUnit(joule / second) double watt = Measure._;
    @DerivedUnit(newton / (meter*meter)) double pascal = Measure._;
    @DerivedUnit(candela * steradian) double lumen = Measure._;
    @DerivedUnit(lumen / (meter*meter)) double lux = Measure._;
    @DerivedUnit(second * ampere) double coulomb = Measure._;
    @DerivedUnit(watt / ampere) double volt = Measure._;
    @DerivedUnit(volt / ampere) double ohm = Measure._;
    @DerivedUnit(coulomb / volt) double farad = Measure._;
    @DerivedUnit(joule / ampere) double weber = Measure._;
    @DerivedUnit(volt * second / (meter*meter)) double tesla = Measure._;
    @DerivedUnit(volt * second / ampere) double henry = Measure._;
    @DerivedUnit(1 / ohm) double siemens = Measure._;
    @DerivedUnit(1 / second) double becquerel = Measure._;
    @DerivedUnit(joule / kilogram) double gray = Measure._;
    @DerivedUnit(joule / kilogram) double sievert = Measure._;
    @DerivedUnit(mole / second) double katal = Measure._;

    @DerivedUnit(kelvin - 273.16) double celsius = Measure._;
}

