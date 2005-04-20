import x10.lang.*;

/**
 * Test for ateach
 *
 * @author: kemal, 12/2004
 * @author vj
 */
public class AtEach2 {
    int nplaces=0;

    public boolean run() {         
        final distribution d=distribution.factory.unique(place.places);
        int[d] disagree = new int[d];
        finish ateach(point p:d) {
            // remember if here and d[i] disagree
            // at any activity at any place
            if (here != d.get(p)) throw new Error("Test failed.");
            async(here){atomic {this.nplaces++;}}             
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        return nplaces==place.MAX_PLACES;
    }
    public static void main(String args[]) {
	boolean b=false;
	try {
        	b= (new AtEach2()).run();
	} catch(Throwable e) {
		b=false;
	}
        System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
        System.exit(b?0:1);
    }
}
