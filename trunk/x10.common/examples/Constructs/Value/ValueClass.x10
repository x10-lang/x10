import x10.lang.*;
/**
 * Value class test:
 *
 * == for value class instances should be implemented by deep
 * recursive, field by field, comparison
 *
 * @author kemal, 12/2004
 *
 */
final value  class myval extends x10.lang.Object {
    // DOES NOT WORK actually writing final causes a compiler error
    /*final*/ int intval;
    /*final*/ complex cval;
    /*final*/ foo refval;
    /*final*/ int value[.] arrayval; // really final (final int)[]
    myval(int intval,complex cval,foo refval, nullable int value[.] arrayval) {
        this.intval=intval;
        this.cval=cval;
        this.refval=refval;
        this.arrayval= arrayval;
    }
}

class foo {
    int w=19;
}

final value  complex extends x10.lang.Object {
    /*final*/ int re;
    /*final*/ int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
    complex add (complex other) {
        return new complex(this.re+other.re,this.im+other.im);
    }
}

public class ValueClass  {


        public boolean run() {

        region r=region.factory.region(0, 9);
        distribution d=distribution.factory.constant(r,here);
        final foo f= new foo();
        myval x= new myval(1,new complex(2,3),f, new int value[d]);
        myval y= new myval(1,new complex(2,3),f, new int value[d]);
        // even if x and y are different objects
        // their fields are equal, and thus they are ==
        System.out.println("1");
        if (x!=y) return false;
        final complex one=new complex(1,1);
        final complex minusone=new complex(-1,-1);
        final complex t=x.cval.add(one).add(minusone);
        y= new myval(x.intval,t,x.refval,x.arrayval);
        // x and y are still equal
        System.out.println("2");
        if (x!=y) return false;
                // objects with different values are not equal
        y= new myval(2,new complex(6,3),f,x.arrayval);
        System.out.println("3");
        if (x==y) return false;
        y=x;
        // x.intval++ // should cause compiler error
        // x.arrayval[0]++ // java will not object
                          // x10 compiler should flag this
        x.refval.w++; // no compiler error
        // ensure foo is treated as a reference object
        // so both x and y see the update
        System.out.println("4");
        if(y.refval.w!=x.refval.w) return false;
        System.out.println("5");
        if(y.refval.w!=20) return false;
        final place P0=here; 
        // the "place" of a value class instance is here
        int n;
        {final myval y0=y;
        n=future(y0.location){
        	( here != P0) ? -1 : y0.intval 
        }.force(); 
        }
        System.out.println("6");
        return n != -1;
        }

	public static void main(String args[]) {
		boolean b= (new ValueClass()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
