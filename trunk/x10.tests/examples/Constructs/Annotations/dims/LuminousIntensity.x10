package dims;

public interface LuminousIntensity extends Measure { 
    @DerivedUnit(SI.candela) const candela: double = _, cd: double = _;
}
