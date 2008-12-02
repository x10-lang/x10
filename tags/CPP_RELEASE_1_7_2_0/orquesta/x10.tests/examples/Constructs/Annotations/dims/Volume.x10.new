package dims;

interface public interface Volume extends Measure {
    @DerivedUnit(Length.cm * Length.cm * Length.cm) var cc: double = _;
    @DerivedUnit(cc) var ml: double = _;
    @DerivedUnit(1000 * ml) var l: double = _;

    @DerivedUnit(Length.inch * Length.inch * Length.inch) var cu_inch: double = _;
    @DerivedUnit(Length.foot * Length.foot * Length.foot) var cu_foot: double = _;

    @DerivedUnit(128 * cu_foot) var cord: double = _;
    @DerivedUnit(42 * gallon) var barrel: double = _;

    @DerivedUnit(tbsp / 3) var tsp: double = _;
    @DerivedUnit(oz / 2) var tbsp: double = _;
    @DerivedUnit(cup / 8) var oz: double = _;
    @DerivedUnit(pint / 2) var cup: double = _;
    @DerivedUnit(quart / 2) var pint: double = _;
    @DerivedUnit(gallon / 4) var quart: double = _;
    @DerivedUnit(gallon / 2) var half_gallon: double = _;
    @DerivedUnit(3.785 * l) var gallon: double = _;
    @DerivedUnit(4 * oz) var gill: double = _;
}
