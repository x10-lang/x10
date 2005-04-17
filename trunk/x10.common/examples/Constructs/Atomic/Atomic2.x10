
/**
 * @author kemal 4/2005
 *
 * atomic enclosing a void function call
 * that throws an exception
 *
 */
public class Atomic2 {

	int x=0;

	public boolean run() {
		finish async(here) {x++;}
		atomic chk(x==1);

		boolean gotException=false;
		try {
		   atomic chk(x==0);
		} catch(Throwable e) {
		   gotException=true;
		}
		chk(gotException);
		return true;
	}

	static void chk(boolean b) {	
		if(!b) throw new Error();
	}

  public static void main (String args[])
  {
    final BoxedBoolean b=new BoxedBoolean();
    try { 
	finish b.val= (new Atomic2()).run();
    } catch (Throwable e) {
	e.printStackTrace();
	b.val=false;
    }
    System.out.println ("++++++ " + (b.val ? "Test succeeded." : "Test failed."));
    System.exit (b.val ? 0 : 1);
  }
}

class BoxedBoolean {
	public boolean val=false;
}
