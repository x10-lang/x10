import harness.x10Test;
public class FSSimpleDistTest extends x10Test {
    public def run():boolean {
	return new FSSimpleDist().run();
    }
    public static def main(Rail[String]) {
	new FSSimpleDistTest().execute();
    }


}
