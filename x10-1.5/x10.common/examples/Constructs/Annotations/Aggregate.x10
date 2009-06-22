import harness.x10Test;
import x10.lang.*;

public class AggregateTest extends harness.x10Test {
    int x=1;
    public boolean run() {
	finish @aggregate { async { this.x=2;}}
	return true;
    }

    public static void main(String[] args) {
        new AggregateTest().execute();
    }
}
