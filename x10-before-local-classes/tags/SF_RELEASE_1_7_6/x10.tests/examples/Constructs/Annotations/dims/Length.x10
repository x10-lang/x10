package dims;

public interface Length extends Measure {
    // SI units
    @DerivedUnit(SI.meter)  const m: double = _;
    @DerivedUnit(1e-12 * m) const pm: double = _;
    @DerivedUnit(1e-10 * m) const angstrom: double = _;
    @DerivedUnit(1e-9  * m) const nm: double = _;
    @DerivedUnit(1e-6  * m) const um: double = _;
    @DerivedUnit(1e-3  * m) const mm: double = _;
    @DerivedUnit(1e-2  * m) const cm: double = _;
    @DerivedUnit(1e3   * m) const km: double = _;

    // astronomical units
    @DerivedUnit(3.08568025e13 * km) const parsec: double = _;
    @DerivedUnit(149597870691. * m) const AU: double = _;
    @DerivedUnit(Velocity.c * Time.year) const lightyear: double = _;
        // 9.4605284e15 * m;

    // English units
    @DerivedUnit(inch / 1000) const thou: double = _;
    @DerivedUnit(2.54 * cm) const inch: double = _;
    @DerivedUnit(12 * inch) const foot: double = _;
    @DerivedUnit(3 * foot) const yard: double = _;
    @DerivedUnit(5.5 * yard) const rod: double = _;
    @DerivedUnit(220 * yard) const furlong: double = _;
    @DerivedUnit(5280 * foot) const mile: double = _;
    @DerivedUnit(3 * mile) const league: double = _;

    @DerivedUnit(2 * yard) const fathom: double = _;
    @DerivedUnit(120 * fathom) const cable: double = _;
    @DerivedUnit(1852 * m) const nautical_mile: double = _;
    @DerivedUnit(8 * furlong) const statute_mile: double = _;

    @DerivedUnit(chain / 100) const link: double = _;
    @DerivedUnit(5.5 * yard) const pole: double = _;
    @DerivedUnit(22 * yard) const chain: double = _;

    // typographic units
    @DerivedUnit(pica / 12) const point: double = _;
    @DerivedUnit(inch / 6) const pica: double = _;
}
