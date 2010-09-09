package dims;

public interface Acceleration extends Measure {
    double @Unit(Length.m / (Time.s * Time.s))  g = 9.8;
}

