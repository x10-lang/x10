package dims;

public interface MagneticFluxDensity extends Measure { 
    @DerivedUnit(SI.tesla) const tesla: double = _, T: double = _;
}
