/**
 *@author kemal, 5/2005
 *
 * Verifying for(point p[i,j]:R){S;} loops can accept
 * continue and break statements.
 *
 * Test resulted in branch target not found message
 * as of 5/26/2005
 *
 */

public class BreakInForTest {
        const int N=100;
	region R = [0:N];
	dist D = dist.factory.arbitrary(R);
	int n1=91;
	int n2=27;
	
	public boolean run() {
                for(int i=0;i<N;i++) {
		     if ((i+1)%n1==0) continue;
		     if ((i+1)%n2==0) break;
		}
	        for(point [i]:D) {
		     if ((i+1)%n1==0) continue;
		     if ((i+1)%n2==0) break;
	        }
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new BreakInForTest()).run();
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
