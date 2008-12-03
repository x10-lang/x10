package dims;

public interface Power extends Measure {
    @DerivedUnit(SI.watt) double watt = _, W = _;
    @DerivedUnit(1000 * W) double kilowatt = _, kW = _;
    @DerivedUnit(745.7 * W) double hp = _;
}

