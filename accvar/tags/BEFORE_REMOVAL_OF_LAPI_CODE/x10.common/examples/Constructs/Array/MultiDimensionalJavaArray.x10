/*
 * @author Igor 1/2006
 * The code below runs as Java but does not compile as x10.  Referring to
 * the indicated line that initializes 'a' it gives,
 * MultiDimensionalJavaArray.x10:15: Cannot assign double[] to double[][].
 * It should compile cleanly.
 * Submitted by Doug Lovell.
 */
public class MultiDimensionalJavaArray {
    private static final int MIN = 100;
    private static final int MAJ = 10;

    public boolean run() {
        double[][] a;
        a = new double[MAJ][MIN];
//        ^ MultiDimensionalJavaArray.x10:15: Cannot assign double[] to double[][].
        for (int i = 0; i < MAJ; ++i)
            for (int j = 0; j < MIN; ++j)
            {
                a[i][j] = i * j / Math.PI;
            }
        double [] d = a[MAJ/2];
        for (int j = 0; j < MIN; ++j)
        {
            chk(d[j] == MAJ/2 * j / Math.PI);
        }
        return true;
    }

    void chk(boolean b) {if(!b) throw new Error(); }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new MultiDimensionalJavaArray()).run();
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

