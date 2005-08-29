

class RegionWithWholes {

    public boolean run() {
	// all of those are contiguous
	region r = [0:10];
	region r1 = [1:2];
	region r2 = [5:6];

	// create wholes in r
	r = r - r1;
	r = r - r2;
	
	short[.] a = new short[r];
	
	// check if r is convex - it should not!
	boolean cv =  r.isConvex();
	System.out.println("convex: " + cv + " (should be false)");
	chk(!cv);
	
	System.out.print("indexes: ");
	for (point[i] : r) {
	    System.out.print(i + " ");
	}
	System.out.println();
	for (point[i] : r) {
	    System.out.println("val[" + i + "]=" + a[i]);
	}
	
	return true;
    }
    
    static void chk(boolean b) {
	if(!b) throw new Error();
    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new RegionWithWholes()).run();
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
