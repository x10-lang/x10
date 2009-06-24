package dims;

public interface Temperature extends Measure {
    @DerivedUnit(SI.kelvin) const kelvin: double = _, K: double = _;
    @DerivedUnit(SI.celsius) const celsius: double = _, C: double = _;
    @DerivedUnit(C * 9. / 5. + 32.) const fahrenheit: double = _, F: double = _;
}
