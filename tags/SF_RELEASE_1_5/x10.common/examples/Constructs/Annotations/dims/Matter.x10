package dims;

public interface Matter extends Measure {
    @DerivedUnit(SI.mole) double mol = _, mole = _;

    double Avogadro = 6.0221415e23;
    double N_A = Avogadro;
}

