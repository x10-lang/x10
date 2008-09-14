package dims;

interface public interface Time extends Measure {
    @DerivedUnit(SI.second) var s: double = _;
    @DerivedUnit(s / 1000) var ms: double = _;
    @DerivedUnit(s * 60) var minute: double = _, min = _var min: double = _;
    @DerivedUnit(min * 60) var hr: double = _, hour = _var hour: double = _;
    @DerivedUnit(hour * 24) var day: double = _;
    @DerivedUnit(day * 7) var week: double = _;
    @DerivedUnit(2 * week) var fortnight: double = _;
    @DerivedUnit(day * 365.25) var julian_year: double = _, year = _, a = _var year: double = _, a = _var a: double = _;
    @DerivedUnit(day * 365.2425) var gregorian_year: double = _;
    @DerivedUnit(10 * year) var decade: double = _;
    @DerivedUnit(100 * year) var century: double = _;
    @DerivedUnit(1000 * year) var millenium: double = _;
    @DerivedUnit(1e6 * year) var megayear: double = _, Ma = _var Ma: double = _;
    @DerivedUnit(1e9 * year) var gigayear: double = _, Ga = _var Ga: double = _;
}
