
/**
 * @author kemal 4/2005
 *
 * Testing block-cyclic dist
 *
 * Randomly generate block-cyclic dists and check 
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 *The dist blockCyclic(R, N, Q) distributes the elements of
 *R cyclically over the set of places Q in blocks of size N.
 *
 */

import java.util.Random;

public class BlockCyclicDist {
	
	public boolean run() {
	final dist P=dist.factory.unique();
	final int np=place.MAX_PLACES;
	final int COUNT=200;
	final int L=5;
	final int K=1;
	   for(point [tries]: [1:COUNT]) {
		final int lb1=ranInt(-L,L);
		final int lb2=ranInt(-L,L);
		final int ub1=ranInt(lb1,L); 
		final int ub2=ranInt(lb2,L);
		final region R = [lb1:ub1,lb2:ub2];
		final int totalPoints=(ub1-lb1+1)*(ub2-lb2+1);
		final int p=totalPoints/np;
		final int bSize=ranInt(1,totalPoints+1);
		
		final dist DBlockCyclic=dist.factory.blockCyclic(R,bSize);
		int[] offsWithinPlace=new int[np];
		int placeNum=0;
		int offsWithinBlock=0;
		// System.out.println("lb1="+lb1+" ub1="+ub1+" lb2="+lb2+" ub2="+ub2+" totalPoints="+totalPoints+" bSize="+bSize);
		
		for(point [i,j]:R) {
		    // System.out.println("placeNum="+placeNum+" offsWithinPlace[placeNum]="+offsWithinPlace[placeNum]+" offsWithinBlock="+offsWithinBlock+" i="+i+" j="+j+" DBlockCyclic[i,j]="+DBlockCyclic[i,j].id);
		    chk(DBlockCyclic[i,j]==P[placeNum]);
		    offsWithinPlace[placeNum]++;
		    offsWithinBlock++;
		    if (offsWithinBlock==bSize) {
			//time to go to next placeNum
			offsWithinBlock=0;
			placeNum++;
			if (placeNum==np) {
			    placeNum=0;
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


    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new BlockCyclicDist()).run();
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
