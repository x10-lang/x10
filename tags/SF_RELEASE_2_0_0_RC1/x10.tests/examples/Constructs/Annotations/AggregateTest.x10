// LIMITATION: aggregate interface not found
import harness.x10Test;

public class AggregateTest extends harness.x10Test {
    var x: int = 1;
    public def run(): boolean = {
	finish @aggregate { async { this.x=2;}}
	return true;
    }

    public static def main(var args: Rail[String]): void = {
        new AggregateTest().execute();
    }
}
