package dims;

public interface Voltage extends Measure { 
    @DerivedUnit(SI.volt) double volt = _, V = _;
}

