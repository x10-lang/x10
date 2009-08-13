package dims;

public interface Velocity extends Measure {
    @DerivedUnit(Length.m/Time.s) const mps: double = _;
    @DerivedUnit(Length.mile/Time.hour) const mph: double = _;
    @DerivedUnit(Length.km/Time.hour) const kph: double = _;
    @DerivedUnit(Length.foot/Time.s) const fps: double = _;
    @DerivedUnit(Length.nautical_mile/Time.hour) const knot: double = _;

    const c: double = 2.99792458E8;
}
