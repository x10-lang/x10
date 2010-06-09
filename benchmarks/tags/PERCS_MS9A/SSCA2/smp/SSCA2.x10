package ssca2;

class SSCA2 {
	
	public static def main (args:Rail[String]!): void {
		
		if (args.length != 2) {
			x10.io.Console.OUT.println("Usage: ./SSCA2 <No. of threads> <SCALE>");
			return;
		}
		
		val THREADS = Int.parseInt(args(0));
		assert THREADS > 0;
		
		util.x10_set_num_threads(THREADS);
		
		val SCALE = Int.parseInt(args(1));
		
		x10.io.Console.OUT.println("HPCS SSCA#2 Graph Analysis Benchmark v2.0");
		x10.io.Console.OUT.println("Running...");
		//     val globals = new defs();
		defs.init(SCALE);
		
		x10.io.Console.OUT.println("# of processors :" + THREADS);
		x10.io.Console.OUT.println("SCALE :" + SCALE);
		
		x10.io.Console.OUT.println("Scalable Data Generator");
		x10.io.Console.OUT.println("genScalData() begining exectuion" );
		
		val genRet = genScaleData.compute();
		x10.io.Console.OUT.println("Time taken for scalable data generation is" + genRet.first);
		
		x10.io.Console.OUT.println("computeGraph begining execution");
		val compRet = computeGraph.compute(genRet.second);
		x10.io.Console.OUT.println("Time taken for kernel 1 is" + compRet.first);
		
		val getRet = getStartLists.compute(compRet.second);
		val tmp: Rail[defs.edge]! = getRet.second;
		x10.io.Console.OUT.println("Time taken for kernel 2 is" + getRet.first);
		
		/* for ((i) in 0..tmp.length-1) {
			 * x10.io.Console.OUT.println("edge " + tmp(i).e + "( " + tmp(i).w + " ) " + " : " + "[ " + tmp(i).startVertex + " , " + tmp(i).endVertex + "]");
		* }  */
		
		val findRet = findSubGraphs.compute(compRet.second, getRet.second as Rail[defs.edge]!);
		x10.io.Console.OUT.println("Time taken for kernel 3 is" + findRet);
	}
	
};
