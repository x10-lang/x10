package dims;

public interface Matter extends Measure {
    @DerivedUnit(SI.mole) const mol: double = _, mole: double = _;

    const Avogadro: double = 6.0221415e23;
    const N_A: double = Avogadro;
}
