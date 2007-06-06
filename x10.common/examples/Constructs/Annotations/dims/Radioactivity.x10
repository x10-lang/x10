package dims;

public interface Frequency extends Measure { 
    @DerivedUnit(SI.becquerel) double becquerel = _, Bq = _;
}

