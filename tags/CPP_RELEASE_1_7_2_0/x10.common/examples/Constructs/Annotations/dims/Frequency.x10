package dims;

public interface Frequency extends Measure { 
    @DerivedUnit(SI.hertz) double hertz = _, Hz = _;
}

