package dims;

public interface Velocity extends Measure {
    @DerivedUnit(Length.m/Time.s) double mps = _;
    @DerivedUnit(Length.mile/Time.hour) double mph = _;
    @DerivedUnit(Length.km/Time.hour) double kph = _;
    @DerivedUnit(Length.foot/Time.s) double fps = _;
    @DerivedUnit(Length.nautical_mile/Time.hour) double knot = _;

    double @Unit(Length.m/Time.s) c = 2.99792458E8;
}
