package dims;

public interface Capacitance extends Measure { 
    @DerivedUnit(SI.farad) double farad = Measure._, F = Measure._;
}

