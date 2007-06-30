package dims;

public interface Resistance extends Measure { 
    @DerivedUnit(SI.ohm) double ohm = _;
}

