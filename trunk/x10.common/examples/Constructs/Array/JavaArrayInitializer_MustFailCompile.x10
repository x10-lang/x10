/**
 * @author kemal 8/2005
 * The x10-style initializer for java arrays was being silently ignored.
 * Language clarification needed. Either reject this kind of initializer
 * for java arrays ([]) or make the initializer work correctly.
 */
public class JavaArrayInitializer_MustFailCompile {
    const int N=25;

    public boolean run() {
        int[.] foo1 = new int[[0:N-1]](point [i]) {return i;};
        System.out.println("1");
        for (point [i]:[0:N-1]) chk(foo1[i]==i);
        int[] foo2 = new int[N] (point [i]) { return i;};
        System.out.println("2");
        for (point [i]:[0:N-1]) chk(foo2[i]==i);
        return true;
    }

    void chk(boolean b) {if(!b) throw new Error(); }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
            finish async b.val=(new JavaArrayInitializer_MustFailCompile()).run();
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

