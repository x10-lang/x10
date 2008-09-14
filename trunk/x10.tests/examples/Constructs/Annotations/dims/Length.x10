package dims;

interface public interface Length extends Measure {
    // SI units
    @DerivedUnit(SI.meter)  var m: double = _;
    @DerivedUnit(1e-12 * m) var pm: double = _;
    @DerivedUnit(1e-10 * m) var angstrom: double = _;
    @DerivedUnit(1e-9  * m) var nm: double = _;
    @DerivedUnit(1e-6  * m) var um: double = _;
    @DerivedUnit(1e-3  * m) var mm: double = _;
    @DerivedUnit(1e-2  * m) var cm: double = _;
    @DerivedUnit(1e3   * m) var km: double = _;

    // astronomical units
    @DerivedUnit(3.08568025e13 * km) var parsec: double = _;
    @DerivedUnit(149597870691. * m) var AU: double = _;
    @DerivedUnit(Velocity.c * Time.year) var lightyear: double = _;
        // 9.4605284e15 * m;

    // English units
    @DerivedUnit(inch / 1000) var thou: double = _;
    @DerivedUnit(2.54 * cm) var inch: double = _;
    @DerivedUnit(12 * inch) var foot: double = _;
    @DerivedUnit(3 * foot) var yard: double = _;
    @DerivedUnit(5.5 * yard) var rod: double = _;
    @DerivedUnit(220 * yard) var furlong: double = _;
    @DerivedUnit(5280 * foot) var mile: double = _;
    @DerivedUnit(3 * mile) var league: double = _;

    @DerivedUnit(2 * yard) var fathom: double = _;
    @DerivedUnit(120 * fathom) var cable: double = _;
    @DerivedUnit(1852 * m) var nautical_mile: double = _;
    @DerivedUnit(8 * furlong) var statute_mile: double = _;

    @DerivedUnit(chain / 100) var link: double = _;
    @DerivedUnit(5.5 * yard) var pole: double = _;
    @DerivedUnit(22 * yard) var chain: double = _;

    // typographic units
    @DerivedUnit(pica / 12) var point: double = _;
    @DerivedUnit(inch / 6) var pica: double = _;
}
