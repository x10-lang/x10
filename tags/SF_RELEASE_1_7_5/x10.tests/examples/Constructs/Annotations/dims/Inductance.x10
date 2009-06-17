package dims;

public interface Inductance extends Measure { 
    @DerivedUnit(SI.henry) const henry: double = _, H: double = _;
}
