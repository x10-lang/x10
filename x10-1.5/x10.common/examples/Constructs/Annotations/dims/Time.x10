package dims;

public interface Time extends Measure {
    @DerivedUnit(SI.second) double s = _;
    @DerivedUnit(s / 1000) double ms = _;
    @DerivedUnit(s * 60) double minute = _, min = _;
    @DerivedUnit(min * 60) double hr = _, hour = _;
    @DerivedUnit(hour * 24) double day = _;
    @DerivedUnit(day * 7) double week = _;
    @DerivedUnit(2 * week) double fortnight = _;
    @DerivedUnit(day * 365.25) double julian_year = _, year = _, a = _;
    @DerivedUnit(day * 365.2425) double gregorian_year = _;
    @DerivedUnit(10 * year) double decade = _;
    @DerivedUnit(100 * year) double century = _;
    @DerivedUnit(1000 * year) double millenium = _;
    @DerivedUnit(1e6 * year) double megayear = _, Ma = _;
    @DerivedUnit(1e9 * year) double gigayear = _, Ga = _;
}

