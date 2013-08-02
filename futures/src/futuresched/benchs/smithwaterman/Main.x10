package futuresched.benchs.smithwaterman;


public class Main {

	public static def main(Rail[String]) {
     val i = 3;
     val j = 3;

     val vs = SmithWaterman1.seqM(i, j);
     Console.OUT.println("Seq: M(" + i + ", " + j + ") = " + vs);

	  val vf = SmithWaterman1.futureM(i, j);
	  //val v = SmithWaterman2.m(i, j);
	  Console.OUT.println("Future: M(" + i + ", " + j + ") = " + vf);

	}
}

