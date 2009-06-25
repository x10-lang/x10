package dims;

public interface Force extends Measure {
    @DerivedUnit(SI.newton) const newton: double = _, N: double = _;
    @DerivedUnit(32.1740485 * Mass.lb * Length.foot / (Time.s*Time.s)) const lbf: double = _;
    @DerivedUnit(Mass.g * Length.cm / (Time.s*Time.s)) const dyn: double = _;
}
