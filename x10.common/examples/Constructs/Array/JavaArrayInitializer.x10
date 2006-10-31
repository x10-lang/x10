import x10.lang.Object;
import harness.x10Test;

/**
* The initializer does not function as expected. 
*
* @author Tong
  10/31/2006
*/
public class JavaArrayInitializer extends x10Test {

        public boolean run() {
            final int size=3;
	    /*The following statment does not function as expected. 
	    After its execution, the elements of Jarray are still zeros*/
            final int [] Jarray=new int [size] (point [i]) {return i;};
	    int sum=0;
	    for (int i=0;i<size;i++) sum+=Jarray[i];
	    //System.out.println("sum="+sum); //sum is zero here.
	    return (sum==6);
	}
	
	public static void main(String[] args) {
		new JavaArrayInitializer().execute();
	}

}

