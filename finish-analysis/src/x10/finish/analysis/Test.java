package x10.finish.analysis;

import java.io.*;

import x10.finish.table.HprofParser;
import x10.finish.table.OutputUtil;

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
	File f = new File("../x10.tests/examples/ScalableFinish/Others/TrivialTest3.x10");
	X10FinishAsyncAnalysis x10fa = new X10FinishAsyncAnalysis();

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
	 x10fa.analyze(f, "","run","()Lx10/lang/Boolean;");
	 //x10fa.analyze(f,"","main","(Lx10/lang/Rail;)V");
	
    }
}
