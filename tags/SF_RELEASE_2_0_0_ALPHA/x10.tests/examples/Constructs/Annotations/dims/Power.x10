package dims;

public interface Power extends Measure {
    @DerivedUnit(SI.watt) const watt: double = _, W: double = _;
    @DerivedUnit(1000 * W) const kilowatt: double = _, kW: double = _;
    @DerivedUnit(745.7 * W) const hp: double = _;
}
