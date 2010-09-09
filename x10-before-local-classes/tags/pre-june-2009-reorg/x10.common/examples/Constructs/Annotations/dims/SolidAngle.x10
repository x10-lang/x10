package dims;

public interface SolidAngle extends Measure { 
    @DerivedUnit(SI.steradian) double steradian = _, sr = _;
}
