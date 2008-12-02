package dims;

public interface MagneticFlux extends Measure { 
    @DerivedUnit(SI.weber) double weber = _, Wb = _;
}

