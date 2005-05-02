import x10.lang.*;
/**
 * Test for ateach
 *
 * @author: kemal, 12/2004
 */
public class AtEach {
    int nplaces=0;

	public boolean run() {         
		final distribution d=distribution.factory.unique(place.places);
		final int[d] disagree = new int[d];
           finish
                ateach(final point p:d) {
                        // remember if here and d[p] disagree
                        // at any activity at any place
			       disagree[p] |= ((here!=d.get(p))?1:0); 
                   async(this){atomic {this.nplaces++;}}             
		         }
                // ensure that d[i] agreed with here in
                // all places
                // and that an activity ran in each place
		return 
             disagree.reduce(intArray.add,0)==0 &&
             nplaces==place.MAX_PLACES;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AtEach()).run();
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
