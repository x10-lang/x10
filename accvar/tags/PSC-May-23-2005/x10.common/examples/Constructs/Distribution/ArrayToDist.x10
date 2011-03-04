
/*
 * Tests conversion of arrays to regions/dists
 * @author kemal 3/2005
 */
class foo {
	public int val;
	public foo(int x) { this.val=x;}
}

public class ArrayToDist {

	const int N=4;
	public boolean run() {
		
		final region R=[0:N-1,0:N-1];
		final dist D= dist.factory.block(R);
		final int[.] A1 =new int[D](point p[i,j]){return f(i,j);};
		final foo[.] A2 =new foo[D](point p[i,j]){return new foo(f(i,j));};
		for(point p[i,j]: A1) 
			chk(f(i,j)==future(A1.distribution[i,j]){A1[i,j]}.force(),"1");
		finish foreach(point p[i,j]: A1) 
			chk(f(i,j)==future(A1.distribution[i,j]){A1[i,j]}.force(),"2");
		finish ateach(point p[i,j]: A1) 
			chk(f(i,j)==A1[i,j],"3");

		for(point p[i,j]: A2) 
			chk(f(i,j)==future(A2.distribution[i,j]){A2[i,j].val}.force(),"4");
		finish foreach(point p[i,j]: A2) 
			chk(f(i,j)==future(A2.distribution[i,j]){A2[i,j].val}.force(),"5");
		finish ateach(point p[i,j]: A2) 
			chk(f(i,j)==A2[i,j].val,"6");
		
		return true;
	}
	static int f(int i, int j){
		return N*i+j;
        }
	static void chk(boolean b, String s) {
		if (!b) throw new Error(s);
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayToDist()).run();
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
