
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
		final int lb1=ranInt(-L,L);
		final int lb2=ranInt(-L,L);
		final int ub1=ranInt(lb1,L); 
		final int ub2=ranInt(lb2,L);

		if ((Math.abs(ub2-lb2) + 1) % np != 0) {
		    // System.out.println("Current implementation allows cyclic distribution only if size of least significant dimension is a multiple of the number of places to cylse over.");
		} else {

		    region R = [lb1:ub1,lb2:ub2];
		    distribution DCyclic=distribution.factory.cyclic(R);
		    final int totalPoints=(ub1-lb1+1)*(ub2-lb2+1);
		    int offsWithinPlace=0;
		    int placeNum=0;
		    // System.out.println("lb1="+lb1+" ub1="+ub1+" lb2="+lb2+" ub2="+ub2+" totalPoints="+totalPoints);
		    
		    for(point [i,j]:R) {
			// System.out.println("placeNum="+placeNum+" offsWithinPlace="+offsWithinPlace+" i="+i+" j="+j+" DCyclic[i,j]="+DCyclic[i,j].id);
			chk(P[placeNum].id==placeNum);
  			chk(DCyclic[i,j]==P[placeNum]);
			placeNum++;
			if (placeNum==np) {
			    //time to go to next offset
			    placeNum=0;
			    offsWithinPlace++;
			}
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
