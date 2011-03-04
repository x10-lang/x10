package dims;

public interface Angle extends Measure { 
    @DerivedUnit(SI.radian) val radian: double = _;
    @DerivedUnit(radian * 180 / Math.PI) var degree: double = _;

    @DerivedUnit(degree / 60) val minute: double = _, min:double = _, arcminute:double = _;
    @DerivedUnit(minute / 60) var second: double = _, sec:double = _, arcsecond:double = _;
    val pi_rad: double@Unit(radian) = Math.PI;
    val two_pi_rad: double@Unit(radian) = 2 * Math.PI;
}
