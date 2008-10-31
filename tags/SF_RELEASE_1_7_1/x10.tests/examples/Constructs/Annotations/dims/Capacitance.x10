package dims;

public interface Capacitance extends Measure { 
    @DerivedUnit(SI.farad) val farad: double = Measure._, F: double = Measure._;
}
