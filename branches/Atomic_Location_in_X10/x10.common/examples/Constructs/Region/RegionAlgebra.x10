
/**
 * @author kemal 4/2005
 *
 * Region algebra
 */

public class RegionAlgebra {
	
	public boolean run() {
		final region R1 = [0:1,0:7];
		final region R2 = [4:5,0:7];
		final region R3 = [0:7,4:5];
		final region T1= (R1 || R2) && R3;
		chk(T1.equals([0:1,4:5] || [4:5,4:5]));
		chk((R1||R2).contains(T1) && R3.contains(T1));
		final region T2= R1 || R2 || R3;
		chk(T2.equals([0:1,0:7]||[4:5,0:7]||
			[2:3,4:5] || [6:7,4:5]));
		chk(T2.contains(R1) &&  T2.contains(R2) &&
			T2.contains(R3));
		final region T3 = (R1||R2)-R3;
		chk(T3.equals([0:1,0:3]||[0:1,6:7] ||
		   [4:5,0:3]||[4:5,6:7]));
		chk((R1||R2).contains(T3) && T3.disjoint(R3));
                return true;
		
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
 	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new RegionAlgebra()).run();
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
