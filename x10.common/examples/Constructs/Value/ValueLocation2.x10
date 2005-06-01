/**
 * Value class location test:
 *
 *
 * Verifying that the location of a value class instance (int) is
 * always 'here', regardless of where the query is made.
 *
 * For example, for the same value class instance x, 
 *
 * at place P0, x.getLocation()==P0
 *
 * at place P1, x.getLocation()==P1
 *
 * but P0 != P1
 *
 * @author kemal, 5/2005
 *
 */

public class ValueLocation2  {
	
	public boolean run() {
		final dist P=dist.factory.unique();
		chk(P.region.equals([0:place.MAX_PLACES-1]));
		chk(P[0]==here);
		final int one=1;
		ateach(point [i]:P) {
			//System.out.println("#1 "+i+" "+one.getLocation()+" "+P[i]+" "+here);
			chk(one.location ==P[i] && P[i]==here);
			chk(here==future(one){here}.force());
		}
		foreach(point[i]:P) {
			//System.out.println("#2 "+i+" "+future(P[i]){one.getLocation()}.force());
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
			finish async b.val=(new ValueLocation2()).run();
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
