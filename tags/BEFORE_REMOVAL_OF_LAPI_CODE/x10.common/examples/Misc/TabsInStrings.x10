/**
 * Ensure that tabs in strings do not cause compilation errors.
 * @author vj, kemal 11/2005
 */
class fmt {
  public static String format(double t) {return ""+t;}
}
public class TabsInStrings {

   public boolean run() {
        double t=25;
        double tmax=200;
        System.out.println("	--> total mg-resid "+fmt.format(t)+
                        " ("+fmt.format(t*100./tmax)+"%)");
        System.out.println("		Hello		world!		");
        return true;
   }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                /*finish async*/ b.val=(new TabsInStrings()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        //x10.lang.Runtime.setExitCode(b.val?0:1);
    }

    static class boxedBoolean {
        boolean val=false;
    }

}
