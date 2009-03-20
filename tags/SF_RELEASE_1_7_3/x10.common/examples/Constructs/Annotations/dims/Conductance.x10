package dims;

public interface Conductance extends Measure { 
    @DerivedUnit(SI.siemens) double siemens = _, S = _;
}

