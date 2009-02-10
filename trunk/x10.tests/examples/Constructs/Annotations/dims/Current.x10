package dims;

public interface Current extends Measure {
    @DerivedUnit(SI.ampere) const ampere: double = _, amp: double = _, A = _var A: double = _;
    @DerivedUnit(1e-3 * A) const milliamp: double = _, mA: double = _;
}
