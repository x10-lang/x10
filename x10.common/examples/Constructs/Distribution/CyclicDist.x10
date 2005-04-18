
/**
 * @author kemal 4/2005
 *
 * Testing cyclic distribution
 *
 * Randomly generate cyclic distributions and check 
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 *The distribution cyclic(R, Q) distributes the points in R
 *cyclically across places in Q in order.
 *
 */

import java.util.Random;

public class CyclicDist {
	
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
		distribution DCyclic=distribution.factory.cyclic(R);
		final int totalPoints=(ub1-lb1+1)*(ub2-lb2+1);
		int offs=0;
		int block=0;
                //System.out.println("lb1="+lb1+" ub1="+ub1+" lb2="+lb2+" ub2="+ub2+" totalPoints="+totalPoints);

		for(point [i,j]:R) {
			//System.out.println("block="+block+" offs="+offs+" i="+i+" j="+j+" P[block]=" +P[block].id+" DCyclic[i,j]="+DCyclic[i,j].id);
  			chk(DCyclic[i,j]==P[block]);
			block++;
			if (block==np) {
				//time to go to next offset
				block=0;
				offs++;
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
		b= (new CyclicDist()).run();
	} catch (Error e) {
		e.printStackTrace();
	}
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
}

}
