import x10.x10rt.X10RT;

public class Topology {
    public static int parent(int i) { return i; }
    public static int nchildren(int i) { return 0; }
    public static int child(int i, int j) { return i; }
    public static boolean is_host(int i) { return true; }
    public static boolean is_spe(int i) { return false; }
    public static boolean is_cuda(int i) { return false; }
    public static String typestr(int p) {
        if (is_host(p)) return "[1mHOST[0m";
        if (is_spe(p)) return "[31mSPE[0m";
        if (is_cuda(p)) return "[32mCUDA[0m";
        return "UNK!";
    }

    public void run(String[] argv) {
        try {
        Thread.currentThread().sleep(X10RT.here()*1000);
        } catch (InterruptedException e) { }

        System.out.println("Nodes: " + X10RT.numPlaces());
        System.out.println("Here: " + X10RT.here());

        System.out.println("-.");
        for (int i = 0; i < X10RT.numPlaces(); ++i) {

            boolean last = i==X10RT.numPlaces()-1;

            System.out.printf((last?"`-":"|-") + "--%02d(%02d)--", i, parent(i), typestr(i));
            if (nchildren(i) > 0) {
                System.out.println("---.");
            }

            System.out.println();

            for (int j = 0; j < nchildren(i); ++j) {
                int p = child(i,j);
                boolean last2 = j==nchildren(i)-1;
                System.out.println((last?"  ":"| ")+"             "+(last2?"`-":"|-")+"--"+j+"--"+p+"("+parent(p)+")--"+typestr(p));
            }

        }
    }
    public static void main(String[] argv) {
        int n = X10RT.numPlaces();
        int i = X10RT.here();
        System.out.println("There are "+n+" Nodes and I am Node "+i);
        new Topology().run(argv);
    }
} 
