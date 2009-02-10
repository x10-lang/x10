package dims;

public interface Conductance extends Measure { 
    @DerivedUnit(SI.siemens) const siemens: double = _, S: double = _;
}
