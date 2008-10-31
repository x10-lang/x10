package dims;

public interface CatalyticActivity extends Measure { 
    @DerivedUnit(SI.katal) double katal = _, kat = _;
}

