package dims;

public interface Illuminance extends Measure { 
    @DerivedUnit(SI.lux) const lux: double = _, lx: double = _;

    @DerivedUnit(10.764 * lux) const footcandle: double = _, fc: double = _;
}
