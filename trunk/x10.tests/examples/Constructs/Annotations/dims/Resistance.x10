package dims;

public interface Resistance extends Measure { 
    @DerivedUnit(SI.ohm) const ohm: double = _;
}
