/**
 * X10 port of jgfutil from Java Grande Forum Benchmark Suite (Version 2.0)
 *
 * @author vj
 *
 */

import sor.*; 
import jgfutil.*; 

public class JGFSORBenchSizeA { 
	
	public boolean run(){
		
		JGFInstrumentor.printHeader(2,0, place.MAX_PLACES);
		
		JGFSORBench sor = new JGFSORBench(); 
		sor.JGFrun(3);
		return true;
		
	}
   /**
    * main method
    */
   
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new JGFSORBenchSizeA()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }
}
