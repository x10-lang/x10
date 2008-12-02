package dims;

public interface Mass extends Measure {
    @DerivedUnit(SI.kilogram) double kg = _;
    @DerivedUnit(1e-3 * kg) double g = _;
    @DerivedUnit(1e-3 * g) double mg = _;
    @DerivedUnit(1e-6 * g) double ug = _;
    @DerivedUnit(1e+3 * kg) double tonne = _;

    @DerivedUnit(kg / 2.2046) double lb = _;
    @DerivedUnit(2000 * lb) double ton = _;
    @DerivedUnit(2240 * lb) double long_ton = _;
    @DerivedUnit(lb / 16) double troy_ounce = _, ounce = _, oz = _;
    @DerivedUnit(oz / 7000) double grain = _;
    @DerivedUnit(oz * 12) double troy_pound = _;
    @DerivedUnit(20 * grain) double scruple = _;
    @DerivedUnit(3 * scruple) double dram = _;
    @DerivedUnit(8 * dram) double apothecary_ounce = _;
    @DerivedUnit(12 * apothecary_ounce) double apothecary_pound = _;
    @DerivedUnit(24 * grain) double pennyweight = _;
    @DerivedUnit(14.594 * kg) double slug = _;
}

