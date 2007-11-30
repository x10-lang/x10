//import x10.lang.Object;
//import harness.x10Test;

/**
*  Implementing a 5-point stencil operation using for loop
* @author Tong 11/29/2006
* Modified by T.W. 11/29/2007: comment out the import statements;
*                              use array initializer;
*                              add more dependent type properties. 
*/
public class StencilFor2D_Dep extends x10Test {
	
        public boolean run() {
        	final region(:rank==2&&rect) R=[-1:256,-1:256], r=[0:255,0:255];
        	final point north=[0,1], south=[0,-1], west=[-1,0], east=[1,0];//rank is not a property of point
        	final double [:rank==2&&rect] A=new double [R] (point [i,j]) {return i+j;};
        	final double h=0.1;
        	
        	for(point p: r) A[p]=(A[p+north]+A[p+south]+A[p+west]+A[p+east]-4*A[p])*h;
        	
	    return true;
	}
	
	public static void main(String[] args) {
		new StencilFor2D_Dep().execute();
	}

}

