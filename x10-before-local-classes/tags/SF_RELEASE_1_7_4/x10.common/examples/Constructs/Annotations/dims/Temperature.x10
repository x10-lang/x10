package dims;

public interface Temperature extends Measure {
    @DerivedUnit(SI.kelvin) double kelvin = _, K = _;
    @DerivedUnit(SI.celsius) double celsius = _, C = _;
    @DerivedUnit(C * 9. / 5. + 32.) double fahrenheit = _, F = _;
}

