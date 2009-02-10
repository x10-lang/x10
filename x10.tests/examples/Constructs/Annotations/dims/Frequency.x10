package dims;

public interface Frequency extends Measure { 
    @DerivedUnit(SI.hertz) const hertz: double = _, Hz: double = _;
}
