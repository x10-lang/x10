package dims;

public interface Illuminance extends Measure { 
    @DerivedUnit(SI.lux) double lux = _, lx = _;

    @DerivedUnit(10.764 * lux) double footcandle = _, fc = _;
}

