package dims;

public interface Pressure extends Measure { 
    @DerivedUnit(SI.pascal) const pascal: double = _, Pa: double = _;
    @DerivedUnit(101.325e3 * Pa) const atm: double = _;
    @DerivedUnit(1e5 * Pa) const bar: double = _;
    @DerivedUnit(133 * Pa) const mmHg: double = _;
    @DerivedUnit(Force.lbf / Area.in2) const psi: double = _;
}
