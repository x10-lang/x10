package dims;

public interface Area extends Measure {
    @DerivedUnit(Length.cm * Length.cm) const cm2: double = _;
    @DerivedUnit(Length.m * Length.m) const m2: double = _;
    @DerivedUnit(100 * m2) const are: double = _;
    @DerivedUnit(Length.km * Length.km) const km2: double = _;

    @DerivedUnit(Length.inch * Length.inch) const inch2: double = _;
    @DerivedUnit(Length.foot * Length.foot) const foot2: double = _;
    @DerivedUnit(Length.mile * Length.mile) const mile2: double = _;

    @DerivedUnit(43560 * Length.foot2) const acre: double = _;
    @DerivedUnit(acre * 100 / 40.5) const hectare: double = _;
    @DerivedUnit(Length.mile * Length.mile) const section: double = _;

    @DerivedUnit(40 * Length.rod * Length.rod) const rood: double = _;
}
