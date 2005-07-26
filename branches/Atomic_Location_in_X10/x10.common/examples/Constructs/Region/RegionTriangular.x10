//LIMITATION: 
//This test case will not meet expectations. It is a limitation of the current release.
/**
 * @author kemal 4/2005
 *
 *Tests upper and lower triangular, and banded regions.
 *
 */

public class RegionTriangular {
	
	public boolean run() {
		final region Universe=[0:7,0:7];
		region upperT=region.factory.upperTriangular(8);
		pr("upperT",upperT);
		for(point [i,j]:Universe)
		  chk(iff(i<=j,upperT.contains([i,j])));
		region lowerT=region.factory.lowerTriangular(8);
		pr("lowerT",lowerT);
		for(point [i,j]:Universe) 
		  chk(iff(i>=j,lowerT.contains([i,j])));
		return true;
        }

	static boolean iff(boolean x, boolean y) {
		return (x==y);
	}

	static void chk(boolean b) {
		if(!b) throw new Error();
 	}
	static void pr(String s,region r) {
		System.out.println();
		System.out.println("printing region "+s);
		int k=0;
		final int N=8;
		for(point [i,j]:[0:N-1,0:N-1]) {
			
			System.out.print(" "+(r.contains([i,j])?"+":"."));		
			if((++k)%N ==0) System.out.println();
		}
	}


	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new RegionTriangular()).run();
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
