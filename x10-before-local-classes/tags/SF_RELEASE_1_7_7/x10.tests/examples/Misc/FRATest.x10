import harness.x10Test;
public class FRATest extends x10Test {
    public def run():boolean {
	return new FRA().run();
    }
    public static def main(args:Rail[String]) {
	new FRATest().execute();
    }


}
