package dims;

interface public interface Matter extends Measure {
    @DerivedUnit(SI.mole) var mol: double = _, mole = _var mole: double = _;

    var Avogadro: double = 6.0221415e23;
    var N_A: double = Avogadro;
}
