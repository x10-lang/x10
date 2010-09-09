/**
 * Exposes a possible parsing bug with
 * if (true) S1 else finish S2; 
 * As of 11/2005, this was executing the finish (it should not) 
 * author vj, kemal 11/2005
 */
public class IfElseFinishBug {

   public boolean run() {
      if (true) System.out.println("True branch");
      else finish foreach(point [i]:[0:1]) {throw new Error("Throwing "+i);} 
      return true;
   }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new IfElseFinishBug()).run();
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
