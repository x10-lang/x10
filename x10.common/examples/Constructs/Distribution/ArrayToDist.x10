
/*
 * Tests conversion of arrays to regions/distributions
 * @author kemal 3/2005
 */
class foo {
	public int val;
	public foo(int x) { this.val=x;}
}

public class ArrayToDist {

	const int N=4;
	public boolean run() {
		
		region R=[0:N-1,0:N-1];
		distribution D= distribution.factory.block(R);
		int[.] A1 =new int[D](point p[i,j]){return f(i,j);};
		foo[.] A2 =new foo[D](point p[i,j]){return new foo(f(i,j));};
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

	public static void main(String args[]) {
		boolean b= (new ArrayToDist()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
