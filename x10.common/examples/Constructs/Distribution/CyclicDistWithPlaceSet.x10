
/**
 * @author kemal 5/2005
 *
 * Testing cyclic dist
 *
 * Randomly generate cyclic dists and check 
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 *The dist cyclic(R, Q) distributes the points in R
 *cyclically across places in Q in order.
 *
 * This test version incorporates actual place sets
 *
 */

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class CyclicDistWithPlaceSet {
	const dist P=dist.factory.unique();
	const int COUNT=200;
	const int L=5;
	
	public boolean run() {
	for(point [tries]:1:COUNT) {
	    final int lb1=ranInt(-L,L);
	    final int lb2=ranInt(-L,L);
	    final int ub1=ranInt(lb1,L); 
	    final int ub2=ranInt(lb2,L);

	    final region R = [lb1:ub1,lb2:ub2];
            final randPlaceSet r=createRandPlaceSet();
            final int np=r.np;
            final int[] placeNums=r.placeNums;
            final Set placeSet=r.placeSet;
	    final dist DCyclic=dist.factory.cyclic(R,placeSet);
	    final int totalPoints=(ub1-lb1+1)*(ub2-lb2+1);
	    int offsWithinPlace=0;
	    int pn=0;
	    //System.out.println("lb1="+lb1+" ub1="+ub1+" lb2="+lb2+" ub2="+ub2+" totalPoints="+totalPoints);
		
            for(point [i,j]:R) {
	        System.out.println("placeNum="+placeNums[pn]+" offsWithinPlace="+offsWithinPlace+" i="+i+" j="+j+" DCyclic[i,j]="+DCyclic[i,j].id);
	        chk(P[placeNums[pn]].id==placeNums[pn]);
		chk(DCyclic[i,j]==P[placeNums[pn]]);
		pn++;
		if (pn==np) {
		    //time to go to next offset
		    pn=0;
		    offsWithinPlace++;
		}
  	    }
        }
        return true;
    }
    /** 
     * emulating multiple return values
     */
    static class randPlaceSet {
        final int np;
        final Set placeSet;
        final int[] placeNums;
        randPlaceSet(int n,int[] a, Set s) {
            np=n;
            placeNums=a;
            placeSet=s;
        }
    }
        
    /**
     * Create a random, non-empty subset of the places 
     */
    randPlaceSet createRandPlaceSet() {
        Set placeSet;
        int np;
        int[] placeNums= new int[place.MAX_PLACES];
        do {
            np=0;
            placeSet=new HashSet();
            final int THRESH=ranInt(10,90);
            for(point [i]:P) {
                final int x=ranInt(0,99);
                if (x>=THRESH) {
                    placeSet.add(P[i]);
                    placeNums[np++]=i;
                }
            }
        } while(np==0);
        return new randPlaceSet(np,placeNums,placeSet);
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
                finish b.val=(new CyclicDistWithPlaceSet()).run();
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
