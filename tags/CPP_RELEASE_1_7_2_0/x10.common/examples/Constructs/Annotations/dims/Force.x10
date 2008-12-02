package dims;

public interface Force extends Measure {
    @DerivedUnit(SI.newton) double newton = _, N = _;
    @DerivedUnit(32.1740485 * Mass.lb * Length.foot / (Time.s*Time.s)) double lbf = _;
    @DerivedUnit(Mass.g * Length.cm / (Time.s*Time.s)) double dyn = _;
}
