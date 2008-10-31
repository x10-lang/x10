package dims;

public interface Acceleration extends Measure {
    var g: double@Unit(Length.m/(Times.s*Times.s)) = 9.8;
}
