package dims;

public interface Charge extends Measure { 
    @DerivedUnit(SI.coulomb) const coulomb: double = _, C: double = _;
}
