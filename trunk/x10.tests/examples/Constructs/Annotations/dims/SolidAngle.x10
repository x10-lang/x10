package dims;

public interface SolidAngle extends Measure { 
    @DerivedUnit(SI.steradian) const steradian: double = _, sr: double = _;
}
