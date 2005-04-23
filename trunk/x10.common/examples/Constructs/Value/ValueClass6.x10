/**
 * Value class test:
 *
 * Tests equality of value arrays whose elements
 * have user defined value and reference types.
 *
 * @author kemal, 4/2004
 *
 */

class foo {
    int w=19;
}

final value  complex {
    int re;
    int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
}

public class ValueClass6  {


        public boolean run() {

        distribution D=[0:9]->here;
	// different value arrays whose elements are the same
	// reference objects, must be equal
	foo value[.] X1=new foo value[D](point p[i]){return new foo();};
	foo value[.] Y1=new foo value[D](point p[i]){return X1[i];};
        System.out.println("1");
        if (X1!=Y1) return false;
	// different value arrays whose elements are different
	// value objects
	// that have the same contents, must be ==
	complex value[.] X2=
          new complex value[D](point p[i]){ return new complex(i,i);};
	complex value[.] Y2=
          new complex value[D](point p[i]){ return new complex(i,i);};
        System.out.println("2");
        if (X2!=Y2) return false;
	// different value arrays whose elements are 
	// different reference objects
	// which have the same contents, must not be ==
	foo value[.] X3=new foo value[D](point p[i]){return new foo();};
	foo value[.] Y3=new foo value[D](point p[i]){return new foo();};
        System.out.println("3");
        if (X3==Y3) return false;
	// different reference arrays must never be ==
	foo[.] X4=new foo[D](point p[i]){return new foo();};
	foo[.] Y4=new foo[D](point p[i]){return X4[i];};
        System.out.println("4");
        if (X4==Y4) return false;
	return true;
        }

	public static void main(String args[]) {
		boolean b= (new ValueClass6()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
