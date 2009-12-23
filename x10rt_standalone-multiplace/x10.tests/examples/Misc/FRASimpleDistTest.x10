import harness.x10Test;
public class FRASimpleDistTest extends x10Test {
    public def run():boolean {
	return new FRASimpleDist().run();
    }
    public static def main(args:Rail[String]) {
	new FRASimpleDistTest().execute();
    }


}
