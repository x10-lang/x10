package dims;

public interface Inductance extends Measure { 
    @DerivedUnit(SI.henry) double henry = _, H = _;
}

