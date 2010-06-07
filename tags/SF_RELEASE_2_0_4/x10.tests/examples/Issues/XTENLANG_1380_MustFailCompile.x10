import harness.x10Test;
public class XTENLANG_1380_MustFailCompile extends x10Test {
	val a:Hello{self!=null} = new Hello();
	val b:Hello{self!=null} = (true ? null : a); // Does not report an error!!!
	//val b:Hello{self!=null} = null; // Correctly reports an error
	
    public static def main(Rail[String]) {
        new XTENLANG_1380_MustFailCompile().execute();
    }
}