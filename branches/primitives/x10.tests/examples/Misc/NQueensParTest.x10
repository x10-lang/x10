import harness.x10Test;
public class NQueensParTest extends x10Test {
    public def run():boolean {
	return NQueensPar.run();
    }
    public static def main(Rail[String]) {
	new NQueensParTest().execute();
    }


}
