/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;

/**
* The member arrays of a class are implicit final?. 
*
* @author Tong
  10/31/2006
*/
public class JavaArrayInitializer extends x10Test {
	int [.] m_array;
	
        public boolean run() {
            final int size=4;
	    /*The following statment does not function as expected. 
	    After its execution, the elements of Jarray are still zeros
            final int [] Jarray=new int [size] (point [i]) {return i;};
	    int sum=0;
	    for (int i=0;i<size;i++) sum+=Jarray[i];
	    //System.out.println("sum="+sum); //sum is zero here.
	    */
	    int [.] array=new int [[0:size]];
	    //the code will pass compilation when the following statement is commented out.
	    //finish ateach (point p [i]: dist.factory.unique()) array[i]=i; 
	    m_array=array;
	    finish ateach (point p [i]: dist.factory.unique()) m_array[i]=i;
	    return true;
	}
	
	public static void main(String[] args) {
		new JavaArrayInitializer().execute();
	}

}

