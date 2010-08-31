package x10.barrier.analysis;

import java.io.*;

public class Test {

    public static void main(String[] args) throws Exception {

	/* ***********************************************************************************
	 * /home/blshao/workspace/wala-bridge-1.0/x10.runtime/src-x10/x10/lang/
	 * /Users/blshao/workspace/wala-bridge-1.0/x10.runtime/src-x10/x10/lang/
	 * Future.x10 /home/blshao/workspace/wala-bridge-1.0/test.x10/Mytest.x10
	 * /home/blshao/workspace/wala-bridge-1.0/x10.runtime/src-x10/x10/array/
	 * PolyScanner.x10
	 * "/blshao/workspace/wala-bridge-1.0/test.x10/RuntimeTest.x10"
	 * FinishAsync/finishTest2
	 * x10.tests/examples/ScalableFinish/Others/TrivialTest3.x10
	 * *************
	 * *********************************************************
	 * ***************
	 */

	X10BarrierAnalysis x10fa = new X10BarrierAnalysis();

	/* *********************************
	 * 
	 * examples of methods' signatures
	 * 
	 * "","main","x10/lang/Rail;" "x10/lang/","run","" "x10/array/", "foo",
	 * "()V" "x10/lang/","foo","()V" "","main","(Lx10/lang/Rail;)V"
	 * "","run","()Lx10/lang/Boolean;
	 * "**********************************
	 */

	// compile(file, package, entrymethod, methodsig)
	//x10fa.compile(f, "","run","()Lx10/lang/Boolean;");
	File f;
	 f = new File("x10.barrier.tests/AllReduceParallelOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/ConvolveOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/HistogramOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/IDEAOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/KMeansScalarOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/LUFactOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/MontyPiParallelOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/ParRandomAccessOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/PipelineOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/SeriesOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/SOROrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/SparseMatMulOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/StencilOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	f = new File("x10.barrier.tests/StreamOrig.x10");
	x10fa.compile(f,"","main","(Lx10/lang/Rail;)V");
	
	
	//OutputUtil.Write2Plot("SimpleFinish5");
	//OutputUtil.Write2Plot("SimpleFinish4");
	//OutputUtil.Write2Plot("SimpleFinish3");
	//OutputUtil.Write2Plot("SimpleFinish2");
	//OutputUtil.Write2Plot("SimpleFinish1");
	//OutputUtil.Write2Plot("ManyLocalFinish2");
	//OutputUtil.Write2Plot("ManyLocalFinish1");
	/*HprofParser p = new HprofParser("/"+user+"/blshao/workspace/x10-compiler/"+
			"x10.tests/examples/ScalableFinish/Patterns/expr2/ManyLocalFinish1/ManyLocalFinish1_8.java.hprof.txt");
	p.parse();
	p.dump(1);
	long all = p.getAllStat();
	long remote = p.getRemoteStat();
	long root = p.getRootStat();
	System.out.println(all+"\t"+root+"\t"+remote);*/
	
    }
}
