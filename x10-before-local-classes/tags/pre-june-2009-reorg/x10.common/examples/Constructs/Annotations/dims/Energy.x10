package dims;

public interface Energy extends Measure {
    @DerivedUnit(SI.joule) double joule = _, J = _;
    @DerivedUnit(Mass.g * Area.cm2 / (Time.s*Time.s)) double erg = _;

    @DerivedUnit(1.602e-19 * J) double eV = _;

    @DerivedUnit(1055.06 * J) double Btu = _;
    @DerivedUnit(Force.lbf * Length.foot) double ft_lbf = _;
    @DerivedUnit(Power.kW * Time.hour) double kWh = _;
    
    @DerivedUnit(4.186 * J) double calorie = _, cal = _;
    @DerivedUnit(1000 * cal) double Calorie = _, kcal = _;
}

