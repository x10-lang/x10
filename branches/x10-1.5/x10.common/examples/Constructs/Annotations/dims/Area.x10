package dims;

public interface Area extends Measure {
    @DerivedUnit(Length.cm * Length.cm) double cm2 = _;
    @DerivedUnit(Length.m * Length.m) double m2 = _;
    @DerivedUnit(100 * m2) double are = _;
    @DerivedUnit(Length.km * Length.km) double km2 = _;

    @DerivedUnit(Length.inch * Length.inch) double inch2 = _;
    @DerivedUnit(Length.foot * Length.foot) double foot2 = _;
    @DerivedUnit(Length.mile * Length.mile) double mile2 = _;

    @DerivedUnit(43560 * Length.foot2) double acre = _;
    @DerivedUnit(acre * 100 / 40.5) double hectare = _;
    @DerivedUnit(Length.mile * Length.mile) double section = _;

    @DerivedUnit(40 * Length.rod * Length.rod) double rood = _;
}

