package dims;

public interface Charge extends Measure { 
    @DerivedUnit(SI.coulomb) double coulomb = _, C = _;
}

