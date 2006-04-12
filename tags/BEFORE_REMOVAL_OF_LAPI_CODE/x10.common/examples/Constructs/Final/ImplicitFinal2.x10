/*
 * Testing new implicit final rule (lack thereof).  
 * @author kemal, 5/2005
 */
public class ImplicitFinal2 {
	
	public boolean run() {
		point p = [1,2,3];
		region r = [10:10];
		point p1 = [1+1,2+2,3+3];
		dist d= r->here;
		p = [1,2,4];
		r = [10:11];
		p1 = [1+1,2+2,4+4];
		d= [0:1]->here;
		point P = [1,2,3];
		region R = [10:10];
		dist D  = R->here;
		P = p;
		R = r;
		D  = d;
		int A=1;
		A=A+1;
		int Bb=1;
		Bb=Bb+1;
		int BB=1;
		BB=BB+BB;
		int c=1;
		c=Bb+c;
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ImplicitFinal2()).run();
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
