/**
 * Static mutable fields are not allowed in x10.
 *
 * (leads to complexities in defining the place of static
 * fields of a class).
 *
 * @author kemal 5/2005
 *
 */

class foo {
    int val;
    foo(int x) { val=x;}
}

public class NoStaticMutable1_MustFailCompile {
    //<== compiler error must occur on next line
    static int x1=0;

    static final int x2=0;
    const int x3=0;

    //<== compiler error must occur on next line
    static foo f1=new foo(1);

    final static foo f2=new foo(1);
    const foo f3=new foo(1);

    public boolean run() {
        x1++;
        f1=new foo(2);
        return true;
    }

    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NoStaticMutable1_MustFailCompile()).run();
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
