

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import com.ibm.wala.cast.x10.analysis.X10FinishAsyncAnalysis;

import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableUtil;
import x10.finish.table.CallTableVal;
import x10.finish.table.HprofParser;
import x10.finish.table.OutputUtil;

public class Test {

    public static void main(String[] args) throws Exception {
	boolean ifSaved = false;
	boolean ifExpanded = false;
	boolean ifStat = false;
	boolean ifDump = false;
	// flags to decide which constructs to expand in the table
	boolean[] mask = { true, true, true };
	HashMap<CallTableKey, LinkedList<CallTableVal>> calltable = null;
	File f = new File("../x10.tests/examples/ScalableFinish/Not_So_Good/TestClosure.x10");
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
	// analyze(file, package, entrymethod, methodsig)	 
	 //x10fa.analyze(f,"","main","(Lx10/lang/Rail;)V");
	calltable = x10fa.analyze(f, "", "run", "()Lx10/lang/Boolean;");
	
	
	
	
	//calltable = CallTableUtil.findPatterns(calltable);
	if (ifStat) {
	    System.out.println("Intitial Table:");
	    CallTableUtil.getStat(calltable);
	}
	if (ifDump) {
	    CallTableUtil.dumpCallTable(calltable);
	}
	if (ifSaved) {
	    System.out.println("saving ... ...");
	    CallTableUtil.saveCallTable("calltable.dat", calltable);
	}
	if (ifExpanded) {
	    System.out.println("Expanding Talbe:");
	    CallTableUtil.expandCallTable(calltable, mask);
	    // CallTableUtil.updateAllArity(calltable);
	    // CallTableUtil.expandCallTable(calltable, mask);
	}
	if (ifStat && ifExpanded) {

	    CallTableUtil.getStat(calltable);
	}
	if (ifDump && ifExpanded) {
	    System.out.println("New Talbe:");
	    CallTableUtil.dumpCallTable(calltable);
	}
	System.out.println("finished ... ");
	
    }
}
