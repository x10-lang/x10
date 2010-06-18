import harness.x10Test;
public class XTENLANG_1380_MustFailCompile extends x10Test {
	val a1:XTENLANG_1380_MustFailCompile{self!=null} = new XTENLANG_1380_MustFailCompile();
	val a2:XTENLANG_1380_MustFailCompile{self!=null} = (true ? null : a1); // ERR
	val a3:XTENLANG_1380_MustFailCompile{self!=null} = null; // ERR
	val a4:XTENLANG_1380_MustFailCompile{self!=null};
	var a5:XTENLANG_1380_MustFailCompile{self!=null}; // ERR
	def this() {
		a4 = new XTENLANG_1380_MustFailCompile();
	}
	
    public static def main(Rail[String]) {
        new XTENLANG_1380_MustFailCompile().execute();
    }
}