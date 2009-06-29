package dims;

public interface MagneticFlux extends Measure { 
    @DerivedUnit(SI.weber) const weber: double = _, Wb: double = _;
}
