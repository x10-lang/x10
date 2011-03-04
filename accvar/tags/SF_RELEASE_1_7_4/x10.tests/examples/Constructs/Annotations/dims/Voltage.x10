package dims;

public interface Voltage extends Measure { 
    @DerivedUnit(SI.volt) const volt: double = _, V: double = _;
}
