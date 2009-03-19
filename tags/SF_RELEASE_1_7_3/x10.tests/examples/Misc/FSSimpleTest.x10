import harness.x10Test;
public class FSSimpleTest extends x10Test {
    public def run():boolean {
	return new FSSimple().run();
    }
    public static def main(Rail[String]) {
	new FSSimpleTest().execute();
    }


}
