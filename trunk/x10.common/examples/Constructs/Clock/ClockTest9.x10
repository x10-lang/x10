
/**
 * Nested barriers test.
 *
 * N outer activities each 
 * create M inner activities that do 
 * barrier syncs. Then the outer activities do a barrier sync 
 * and check the results.
 *
 * @author kemal 4/2005
 */
public class ClockTest9 {

	const int N=8;
	const int M=8;
	final int[.] val=new int[0:N-1];

	public boolean run() {
          finish async {
      		final clock c = clock.factory.clock();
		
		foreach (point [i]: 0:N-1) clocked(c) {
				foreachBody(i,c);
		}
          }
          return true;
	}

	void foreachBody(final int i, final clock c) {
          now (c) async {
      		final clock d = clock.factory.clock();
		
		foreach (point [j]: 0:M-1) clocked(d) {
				foreachBodyInner(i,j,d);
		}
          }
          System.out.println("#0a i="+i);
	  next;
	  now(c) for(point [j]:val) chk(val[j]==0);
          System.out.println("#0b i="+i);
	  next;
	}
        void foreachBodyInner(final int i, final int j, final clock d) {
	  now(d) {atomic val[i] += j;}
          System.out.println("#1 i="+i+" j="+j);
	  next;
 	  now(d) {int tmp; atomic tmp=val[i];chk(tmp==M*(M-1)/2);}
          System.out.println("#2 i="+i+" j="+j);
 	  next;
	  now (d) {atomic val[i] -= j; }
          System.out.println("#3 i="+i+" j="+j);
	  next;
        }

        static void chk(boolean b) { 
	  if (!b) throw new Error();
        }
         
	public static void main(String args[]) {
		boolean b= (new ClockTest9()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
