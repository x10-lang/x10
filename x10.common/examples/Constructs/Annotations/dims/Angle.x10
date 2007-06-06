package dims;

public interface Angle extends Measure { 
    @DerivedUnit(SI.radian) double radian = _;
    @DerivedUnit(radian * 180 / Math.PI) double degree = _;

    @DerivedUnit(degree / 60) double minute = _, min = _, arcminute = _;
    @DerivedUnit(minute / 60) double second = _, sec = _, arcsecond = _;

    double @Unit(radian) pi_rad = Math.PI;
    double @Unit(radian) two_pi_rad = 2 * Math.PI;
}

