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
        final dist d=dist.factory.unique(place.places);
        int[d] disagree = new int[d];
        finish ateach(point p:d) {
            // remember if here and d[i] disagree
            // at any activity at any place
            if (here != d.get(p)) throw new Error("Test failed.");
            async(this.location){atomic {this.nplaces++;}}             
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        return nplaces==place.MAX_PLACES;
    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AtEach2()).run();
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
