package dims;

public interface Current extends Measure {
    @DerivedUnit(SI.ampere) double ampere = _, amp = _, A = _;
    @DerivedUnit(1e-3 * A) double milliamp = _, mA = _;
}

