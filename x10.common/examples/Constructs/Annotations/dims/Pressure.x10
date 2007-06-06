package dims;

public interface Pressure extends Measure { 
    @DerivedUnit(SI.pascal) double pascal = _, Pa = _;
    @DerivedUnit(101.325e3 * Pa) double atm = _;
    @DerivedUnit(1e5 * Pa) double bar = _;
    @DerivedUnit(133 * Pa) double mmHg = _;
    @DerivedUnit(Force.lbf / Area.in2) double psi = _;
}

