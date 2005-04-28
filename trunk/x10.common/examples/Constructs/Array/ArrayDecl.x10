/*
 * @author kemal 4/2005
 *
 * Array declarations using N instead of (0:N-1)
 *
 */
public class ArrayDecl {

const int N=24;

public boolean run() {
		
	final int[.] ia0 = new int[[0:N-1]->here];
	final place p = here;
	chk(ia0.distribution.equals([0:N-1]->p));
	chk(ia0.distribution.equals((0:N-1)->p));
	finish ateach(point [i]:ia0) chk(ia0[i]==0); 

	final int value[.] v_ia2 = new int value[[0:N-1]->here]
	      (point [i]){return i;};
	chk(v_ia2.distribution.equals([0:N-1]->here));
	for(point [i]:v_ia2) chk(v_ia2[i]==i); 
   
	final byte[.] ia2 = new byte[[0:N-1]->(here)];
	chk(ia2.distribution.equals([0:N-1]->p.prev().prev()));
	finish ateach(point [i]:ia2) chk(ia2[i]==(byte)0);

	//Examples similar to section 10.3 of X10 reference manual

	final double[.] data1 = new double[[0:16]->here] 
	  new doubleArray.pointwiseOp()
	    {public double apply(point p[i]){return (double)i;}};
	chk(data1.distribution.equals([0:16]->here));
	for(point [i]:data1) chk(data1[i]==(double)i);

	final String myStr="abcdefghijklmnop";
	final char[.] data2 = new char value [[1:2,1:3]->here]
	   (point p[i,j]){return myStr.charAt(i*j);};
	chk(data2.distribution.equals([1:2,1:3]->here));
	for(point [i,j]:data2) chk(data2[i,j]==myStr.charAt(i*j));

	// is a region R converted to R->here in a distribution context? 
	//final long[.] data3 = new long[1:11] 
	final long[.] data3 = new long[(1:11)->here] 
	  (point [i]){return (long)i*i;};
	chk(data3.distribution.equals([1:11]->here));
	for(point [i]:data3) chk(data3[i]==(long)i*i);

	final distribution D=distribution.factory.random([0:9]);
	final float[D] d= new float[D] 
	  (point [i]){return (float)(10.0*i);};
	chk(d.distribution.equals(D));
	finish ateach(point [i]:d) chk(d[i]==(float)(10.0*i));

	final distribution E=distribution.factory.random([1:7,0:1]);
	final short[E] result1 = new short[E]
	    (point [i,j]) {return (short)(i+j);};
	chk(result1.distribution.equals(E));
	finish ateach(point [i,j]:E) chk(result1[i,j]==(short)(i+j));

	final complex[.] result2 = new complex[N->here]
	    (point [i]) {return new complex(i*N,-i);};
	chk(result2.distribution.equals([0:N-1]->here));
	finish ateach(point [i]:result2) chk(result2[i]==new complex(i*N,-i));

	return true;
}


 	/**
	 * Throw an error if b is false
	 */
	static void chk(boolean b) {

		if(!b) throw new Error();
	}

	/**
	 * Main method
	 */
	public static void main(String args[]) {
		boolean b=false;
		try{
			b= (new ArrayDecl()).run();
		} catch(Throwable e) {
			e.printStackTrace();
			b=false;
		}
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}

final value  complex {
    int re;
    int im;
    public complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
}
