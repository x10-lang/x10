
/**
 * @author kemal 4/2005
 * Testing block distribution
 *
 * Randomly generate block distributions and check 
 * index-to-place mapping for conformance with x10 0.41 spec
 *
 *The distribution block(R, Q) distributes the elements of R (in
 *order) over the set of places Q in blocks as follows. Let p equal
 *|R| div N and q equal |R| mod N, where N is the size of Q. The first
 *q places get successive blocks of size (p + 1) and the remaining
 *places get blocks of size p.
 *
 */

import java.util.Random;

public class BlockDist {
	
	public boolean run() {
	distribution P=distribution.factory.unique();
	final int np=place.MAX_PLACES;
	final int COUNT=200;
	final int L=5;
	   for(point [tries]:1:COUNT) {
		int lb1=ranInt(-L,L);
		int lb2=ranInt(-L,L);
		int ub1=ranInt(lb1,L); 
		int ub2=ranInt(lb2,L);
		region R = [lb1:ub1,lb2:ub2];
		distribution DBlock=distribution.factory.block(R);
		final int totalPoints=(ub1-lb1+1)*(ub2-lb2+1);
		final int p=totalPoints/np;
		final int q=totalPoints%np;
		int offs=0;
		int block=0;
                //System.out.println("lb1="+lb1+" ub1="+ub1+" lb2="+lb2+" ub2="+ub2+" totalPoints="+totalPoints+" p="+p+" q="+q);

		for(point [i,j]:R) {
			//System.out.println("block="+block+" offs="+offs+" i="+i+" j="+j+" P[block]=" +P[block].id+" DBlock[i,j]="+DBlock[i,j].id);
  			chk(DBlock[i,j]==P[block]);
			offs++;
			if (offs==(p+((block<q)?1:0))) {
				//time to go to next block
				offs=0;
				block++;
			}
		}
            }
	    return true;

	}

	void chk(boolean b) {if(!b) throw new Error();}

private Random myRand=new Random(1L);

/**
 * return a random integer between lb and ub (inclusive)
 */
private int ranInt(int lb,int ub) {
	return lb+myRand.nextInt(ub-lb+1);
}

public static void main(String args[]) {
	boolean b=false;
	try {
		b= (new BlockDist()).run();
	} catch (Error e) {
		e.printStackTrace();
	}
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
}

}
