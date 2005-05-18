/**
 * Automatic boxing and un-boxing of a final value class
 * when up-casting, and down-casting
 */
public class Boxing1 {
    public boolean run() {
        x10.lang.Object o = X.five();
        System.out.println("int");
        if (!(o instanceof int)) return false;
        System.out.println("double");
        if (o instanceof double) return false;
        int i= (int) o + 1;
        System.out.println("6");
        if (i!=6) return false;
        _dummy d=new _complex(1,2);
        o=d;
        System.out.println("d _complex");
        if (!(d instanceof _complex)) return false;
        System.out.println("o _dummy");
        if (!(o instanceof _dummy)) return false;
        System.out.println("o _complex");
        if (!(o instanceof _complex)) return false;
        _dummy d2=new _dummy();
        System.out.println("d2 _complex");
        if (d2 instanceof _complex) return false;
        _complex c= ((_complex)d).add(new _complex(1,1));
        System.out.println("c _complex");
        if (c !=(new _complex(2,3))) return false;
        return true;
    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Boxing1()).run();
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
    public static int five() {return 5;}
}



