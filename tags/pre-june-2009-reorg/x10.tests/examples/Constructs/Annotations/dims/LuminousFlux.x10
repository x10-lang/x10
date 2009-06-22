package dims;

public interface LuminousFlux extends Measure { 
    @DerivedUnit(SI.lumen) const lumen: double = _, lm: double = _;
}
