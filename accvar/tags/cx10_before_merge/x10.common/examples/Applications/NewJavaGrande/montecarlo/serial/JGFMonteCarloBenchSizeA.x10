/**
  * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0)
  *
  * @author Vivek Sarkar (vsarkar@us.ibm.com)
  * @author vj
  */
  
import montecarlo.*;
import jgfutil.*;
  public class JGFMonteCarloBenchSizeA  { 

  public boolean run() {

    JGFInstrumentor.printHeader(3,0);

    JGFMonteCarloBench mc = new JGFMonteCarloBench(); 
    mc.JGFrun(0);
    return true;
 
  }
   /**
    * main method
    */
   
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new JGFMonteCarloBenchSizeA()).run();
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
 
