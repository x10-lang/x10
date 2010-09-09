package dims;

public interface Capacitance extends Measure { 
    @DerivedUnit(SI.farad) const farad: double = Measure._, F: double = Measure._;
}
