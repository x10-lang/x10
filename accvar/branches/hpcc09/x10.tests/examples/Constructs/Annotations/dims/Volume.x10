package dims;

public interface Volume extends Measure {
    @DerivedUnit(Length.cm * Length.cm * Length.cm) const cc: double = _;
    @DerivedUnit(cc) const ml: double = _;
    @DerivedUnit(1000 * ml) const l: double = _;

    @DerivedUnit(Length.inch * Length.inch * Length.inch) const cu_inch: double = _;
    @DerivedUnit(Length.foot * Length.foot * Length.foot) const cu_foot: double = _;

    @DerivedUnit(128 * cu_foot) const cord: double = _;
    @DerivedUnit(42 * gallon) const barrel: double = _;

    @DerivedUnit(tbsp / 3) const tsp: double = _;
    @DerivedUnit(oz / 2) const tbsp: double = _;
    @DerivedUnit(cup / 8) const oz: double = _;
    @DerivedUnit(pint / 2) const cup: double = _;
    @DerivedUnit(quart / 2) const pint: double = _;
    @DerivedUnit(gallon / 4) const quart: double = _;
    @DerivedUnit(gallon / 2) const half_gallon: double = _;
    @DerivedUnit(3.785 * l) const gallon: double = _;
    @DerivedUnit(4 * oz) const gill: double = _;
}
