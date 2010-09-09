import harness.x10Test;

/**
 * 
 *
 * @author vj
 */
public class EntailsPositiveMustFailCompile(int i, int j) extends x10Test {

	public EntailsPositiveMustFailCompile(int i, int j) { this.i=i; this.j=j;}
	public boolean run() {
	    EntailsPositiveMustFailCompile(: i==1) x =  new EntailsPositiveMustFailCompile(1,2);
	    return true;
	}
	public static void main(String[] args) {
		new EntailsPositiveMustFailCompile(1,2).execute();
	}
}

