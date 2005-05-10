/**
 * Value class location test:
 *
 *
 * Verifying that the location of a value class instance is
 * always 'here', regardless of where the query is made.
 *
 * For example, for the same value class instance x, 
 *
 * at place P[0], x.location==P[0]
 *
 * at place P[1], x.location==P[1]
 *
 * but P[0] != P[1]
 *
 * @author kemal, 5/2005
 *
 */

final value  complex {
    int re;
    int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
}

public class ValueLocation  {

    public boolean run() {

	final distribution P=distribution.factory.unique();
	chk(P.region.equals([0:place.MAX_PLACES-1]));
        final complex one=new complex(1,1);
	ateach(point [i]:P) {
		//System.out.println("#1 "+i+" "+one.location+" "+P[i]+" "+here);
		chk(one.location==P[i] && P[i]==here);
	}
	foreach(point[i]:P) {
	  //System.out.println("#2 "+i+" "+future(P[i]){one.location}.force());
          foreach(point[j]:P) {
	       //System.out.println("#3 "+i+" "+j+" "+P[i]+" "+P[j]);
               chk(implies(P[i]==P[j],i==j));
	  }
	  chk(P[i]==future(P[i]){one.location}.force());
        }

        return true;
    }

    static void chk(boolean b) {if(!b) throw new Error();}

    static boolean implies(boolean x, boolean y) {return (!x)|y;}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ValueLocation()).run();
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
