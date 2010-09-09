package dims;

public interface CatalyticActivity extends Measure { 
    @DerivedUnit(SI.katal) const katal: double = _, kat: double = _;
}
