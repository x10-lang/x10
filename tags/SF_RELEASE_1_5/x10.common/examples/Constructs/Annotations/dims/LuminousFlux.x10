package dims;

public interface LuminousFlux extends Measure { 
    @DerivedUnit(SI.lumen) double lumen = _, lm = _;
}

