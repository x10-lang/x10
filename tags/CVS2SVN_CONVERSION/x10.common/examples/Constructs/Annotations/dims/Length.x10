package dims;

public interface Length extends Measure {
    // SI units
    @DerivedUnit(SI.meter)  double m = _;
    @DerivedUnit(1e-12 * m) double pm = _;
    @DerivedUnit(1e-10 * m) double angstrom = _;
    @DerivedUnit(1e-9  * m) double nm = _;
    @DerivedUnit(1e-6  * m) double um = _;
    @DerivedUnit(1e-3  * m) double mm = _;
    @DerivedUnit(1e-2  * m) double cm = _;
    @DerivedUnit(1e3   * m) double km = _;

    // astronomical units
    @DerivedUnit(3.08568025e13 * km) double parsec = _;
    @DerivedUnit(149597870691. * m) double AU = _;
    @DerivedUnit(Velocity.c * Time.year) double lightyear = _;
        // 9.4605284e15 * m;

    // English units
    @DerivedUnit(inch / 1000) double thou = _;
    @DerivedUnit(2.54 * cm) double inch = _;
    @DerivedUnit(12 * inch) double foot = _;
    @DerivedUnit(3 * foot) double yard = _;
    @DerivedUnit(5.5 * yard) double rod = _;
    @DerivedUnit(220 * yard) double furlong = _;
    @DerivedUnit(5280 * foot) double mile = _;
    @DerivedUnit(3 * mile) double league = _;

    @DerivedUnit(2 * yard) double fathom = _;
    @DerivedUnit(120 * fathom) double cable = _;
    @DerivedUnit(1852 * m) double nautical_mile = _;
    @DerivedUnit(8 * furlong) double statute_mile = _;

    @DerivedUnit(chain / 100) double link = _;
    @DerivedUnit(5.5 * yard) double pole = _;
    @DerivedUnit(22 * yard) double chain = _;

    // typographic units
    @DerivedUnit(pica / 12) double point = _;
    @DerivedUnit(inch / 6) double pica = _;
}

