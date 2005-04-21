/**
 * Value class test:
 *
 * == for value class instances should be implemented by deep
 * recursive, field by field, comparison
 *
 * Temporarily == is implemented by a user defined method, to
 * debug other features of value classes
 *
 * @author kemal, 12/2004
 *
 */
final value  class myval {
    int intval;
    complex cval;
    foo refval;
    int value[.] arrayval;
    myval(int intval,complex cval,foo refval, int value[.] arrayval) {
        this.intval=intval;
        this.cval=cval;
        this.refval=refval;
        this.arrayval= arrayval;
    }
    boolean _equals(myval other) {
	return 
	  this.intval == other.intval &&
	  this.cval._equals(other.cval) &&
          this.refval==other.refval &&
	  ArrayEquals(this.arrayval,other.arrayval);
    }
    static boolean ArrayEquals(int value[.] a, int value[.] b) {
	if (!a.region.equals(b.region)) throw new Error();
	for(point p[i]:a.region) {
		if (a[i]!=b[i]) return false;
	}
	return true;
    }


}

class foo {
    int w=19;
}

final value  complex  {
    int re;
    int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
    complex add (complex other) {
        return new complex(this.re+other.re,this.im+other.im);
    }
    boolean _equals(complex other) {
	return (other.re == this.re &&
		other.im == this.im);
    }
}

public class ValueClassUserDefinedEquals  {


        public boolean run() {

        region r=region.factory.region(0, 9);
        distribution d=distribution.factory.constant(r,here);
        final foo f= new foo();
        myval x= new myval(1,new complex(2,3),f, new int value[d]);
        myval y= new myval(1,new complex(2,3),f, new int value[d]);
        // even if x and y are different objects
        // their fields are equal, and thus they are ==
        if (!x._equals(y)) return false;
        final complex one=new complex(1,1);
        final complex minusone=new complex(-1,-1);
        final complex t=x.cval.add(one).add(minusone);
        y= new myval(x.intval,t,x.refval,x.arrayval);
        // x and y are still equal
        if (!x._equals(y)) return false;
                // objects with different values are not equal
        y= new myval(2,new complex(6,3),f,x.arrayval);
        if (x._equals(y)) return false;
        y=x;
        x.refval.w++;
        // ensure foo is treated as a reference object
        // so both x and y see the update
        if(y.refval.w!=x.refval.w) return false;
        if(y.refval.w!=20) return false;
        final place P0=here; 
        // the "place" of a value class instance is here
        int n;
        {final myval y0=y;
        n=future(y0.location){
        	( here != P0) ? -1 : y0.intval 
        }.force(); 
        }
        return n != -1;
        }

	public static void main(String args[]) {
		boolean b= (new ValueClassUserDefinedEquals()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
