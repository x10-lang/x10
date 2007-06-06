package dims;

public interface Volume extends Measure {
    @DerivedUnit(Length.cm * Length.cm * Length.cm) double cc = _;
    @DerivedUnit(cc) double ml = _;
    @DerivedUnit(1000 * ml) double l = _;

    @DerivedUnit(Length.inch * Length.inch * Length.inch) double cu_inch = _;
    @DerivedUnit(Length.foot * Length.foot * Length.foot) double cu_foot = _;

    @DerivedUnit(128 * cu_foot) double cord = _;
    @DerivedUnit(42 * gallon) double barrel = _;

    @DerivedUnit(tbsp / 3) double tsp = _;
    @DerivedUnit(oz / 2) double tbsp = _;
    @DerivedUnit(cup / 8) double oz = _;
    @DerivedUnit(pint / 2) double cup = _;
    @DerivedUnit(quart / 2) double pint = _;
    @DerivedUnit(gallon / 4) double quart = _;
    @DerivedUnit(gallon / 2) double half_gallon = _;
    @DerivedUnit(3.785 * l) double gallon = _;
    @DerivedUnit(4 * oz) double gill = _;
}

