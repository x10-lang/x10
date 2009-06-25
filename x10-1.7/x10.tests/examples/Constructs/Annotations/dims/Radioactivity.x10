package dims;

public interface Frequency extends Measure { 
    @DerivedUnit(SI.becquerel) const becquerel: double = _, Bq: double = _;
}
