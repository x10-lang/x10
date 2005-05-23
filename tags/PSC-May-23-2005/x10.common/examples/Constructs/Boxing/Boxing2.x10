/**
 * Tests interaction of parentheses and boxing
 * 
 */
public class Boxing2 {
    public boolean run() {
        String x="The number is "+(X.five()*2);
        if (!x.equals("The number is 10")) return false;
        String y="The number is "+(200+X.five()*2);
        if (!y.equals("The number is 210")) return false;
        return true;
    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Boxing2()).run();
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

class X {
    public static int five() { return 5;}
}
