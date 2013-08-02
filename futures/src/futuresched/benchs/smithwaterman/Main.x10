package futuresched.benchs.smithwaterman;


public class Main {

	public static def main(Rail[String]) {
     val i = 8;
     val j = 8;

     val st1 = System.currentTimeMillis();
     val vs = SmithWaterman1.seqM(i, j);
     val st2 = System.currentTimeMillis();
     Console.OUT.println("Seq: M(" + i + ", " + j + ") = " + vs);
     Console.OUT.println("Time = " + (st2 - st1));

     val ft1 = System.currentTimeMillis();
     val vf = SmithWaterman1.futureM(i, j);
     val ft2 = System.currentTimeMillis();
     Console.OUT.println("Future: M(" + i + ", " + j + ") = " + vf);
     Console.OUT.println("Time = " + (ft2 - ft1));

//
//     val vs = SmithWaterman2.seqM(i, j);
//     Console.OUT.println("Seq: M(" + i + ", " + j + ") = " + vs);
//	  val vf = SmithWaterman2.futureM(i, j);
//	  Console.OUT.println("Future: M(" + i + ", " + j + ") = " + vf);


	}
}

