/**
 * Use Dekker's test to confirm strong ordering of
 * atomic sections. (Failure of test proves lack of
 * ordering).
 * @author kemal 4/2005
 */

class pair {
int v1;
int v2;
}


class AtomicOrdered {

const int CACHESIZE=32*1024/4;
const int LINESIZE=128/4;
const int MAX_ASSOC=8;

final int[.] A =new int[[0:CACHESIZE*(MAX_ASSOC+2)-1]->here];

static void  chk(boolean b) {
	if(!b) throw new Error();
}

public boolean run() {
	final pair r=new pair();
	finish {
		async(here) {
			finish {} // delay
			atomic A[0]=1;
			int t;
			atomic t=A[LINESIZE];
			r.v1=t;
		}
		async(here) {
			finish {} // delay
			atomic A[LINESIZE]=1;
			int t;
			atomic t=A[0];
			r.v2=t;
		}
	}
	System.out.println("v1="+r.v1+" v2="+r.v2);
	// not both could have read the old value
	atomic chk(!(r.v1==0 && r.v2==0));
 	return true;
}

public static void main(String[] args) { 
	boolean b=false;
	try {
		b=(new AtomicOrdered()).run();
	} catch(Throwable e) {
		e.printStackTrace();
		b=false;
	}
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
}
}
	
