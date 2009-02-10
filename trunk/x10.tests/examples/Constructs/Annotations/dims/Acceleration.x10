package dims;

public interface Acceleration extends Measure {
    const g: double@Unit(Length.m/(Times.s*Times.s)) = 9.8;
}
