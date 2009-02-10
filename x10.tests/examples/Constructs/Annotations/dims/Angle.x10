package dims;

public interface Angle extends Measure { 
    @DerivedUnit(SI.radian) const radian: double = _;
    @DerivedUnit(radian * 180 / Math.PI) const degree: double = _;

    @DerivedUnit(degree / 60) const minute: double = _, min:double = _, arcminute:double = _;
    @DerivedUnit(minute / 60) const second: double = _, sec:double = _, arcsecond:double = _;
    const pi_rad: double@Unit(radian) = Math.PI;
    const two_pi_rad: double@Unit(radian) = 2 * Math.PI;
}
